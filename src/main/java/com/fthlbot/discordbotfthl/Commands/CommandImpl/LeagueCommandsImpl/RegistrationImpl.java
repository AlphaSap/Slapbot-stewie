package com.fthlbot.discordbotfthl.Commands.CommandImpl.LeagueCommandsImpl;

import Core.Enitiy.clan.ClanModel;
import Core.exception.ClashAPIException;
import com.fthlbot.discordbotfthl.core.Annotation.AllowedChannel;
import com.fthlbot.discordbotfthl.core.Annotation.CommandType;
import com.fthlbot.discordbotfthl.core.Annotation.Invoker;
import com.fthlbot.discordbotfthl.Commands.CommandListener.RegistrationListener;
import com.fthlbot.discordbotfthl.DatabaseModels.Division.Division;
import com.fthlbot.discordbotfthl.DatabaseModels.Division.DivisionService;
import com.fthlbot.discordbotfthl.DatabaseModels.Exception.LeagueException;
import com.fthlbot.discordbotfthl.DatabaseModels.Team.Team;
import com.fthlbot.discordbotfthl.DatabaseModels.Team.TeamService;
import com.fthlbot.discordbotfthl.Util.BotConfig;
import com.fthlbot.discordbotfthl.Util.Exception.ClashExceptionHandler;
import org.javacord.api.entity.channel.ChannelCategory;
import org.javacord.api.entity.channel.ServerTextChannel;
import org.javacord.api.entity.channel.ServerTextChannelBuilder;
import org.javacord.api.entity.channel.TextChannel;
import org.javacord.api.entity.message.embed.EmbedBuilder;
import org.javacord.api.entity.permission.PermissionType;
import org.javacord.api.entity.permission.PermissionsBuilder;
import org.javacord.api.entity.server.Server;
import org.javacord.api.entity.user.User;
import org.javacord.api.event.interaction.SlashCommandCreateEvent;
import org.javacord.api.interaction.SlashCommandInteraction;
import org.javacord.api.interaction.SlashCommandInteractionOption;
import org.javacord.api.interaction.callback.InteractionOriginalResponseUpdater;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.awt.*;
import java.io.IOException;
import java.text.ParseException;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

import static com.fthlbot.discordbotfthl.DiscordBotFthlApplication.clash;
import static com.fthlbot.discordbotfthl.Util.GeneralService.*;

@Component
@Invoker(
        alias = "register",
        description = "A simple registration command!",
        usage = "/register <CLAN TAG> <DIVISION ALIAS> <TEAM ALIAS> <@Second Rep (optional)>",
        type = CommandType.REGISTRATION,
        where = AllowedChannel.APPLICANT_SERVER
)
/* TODO
 * Notes to myself -
 *
 * make a channel
 * initial success message
 * send a message in channel (regarding other useful commands)
 * tag the reps in the channel
 * insert the team into the database
 *
 *      **Exceptions**
 * team already exists (in the same division) - clan included!
 * clan tag
 * not enough arguments provided
 * SQL exceptions too
 */


//Make slash commands, /register <clan tag> <div alias> <team alias> <optional @second rep>
public class RegistrationImpl implements RegistrationListener {
    private final DivisionService divisionService;
    private final TeamService teamService;
    private final BotConfig config;
    //Logger
    private final Logger logger = LoggerFactory.getLogger(RegistrationImpl.class);


    public RegistrationImpl(DivisionService divisionService, TeamService teamService, BotConfig config) {
        this.divisionService = divisionService;
        this.teamService = teamService;
        this.config = config;
    }

    @Override
    public void execute(SlashCommandCreateEvent event) {
        SlashCommandInteraction slashCommandInteraction = event.getSlashCommandInteraction();
        CompletableFuture<InteractionOriginalResponseUpdater> respond = slashCommandInteraction.respondLater();

        //check if today is between league start date and registration start date from config
        if (!isRegistrationOpen()) {
            //TODO: check if the person trying to register is a staff
            respond.thenAccept(res -> res.setContent("Registration is closed").update());
            return;
        }
        long registrationChannelID = config.getRegistrationChannelID();
        Optional<TextChannel> channel = event.getSlashCommandInteraction().getChannel();

        if (channel.isEmpty()){
            respond.thenAccept(res -> res.setContent("This command can only be executed in a Text Channel").update());
            return;
        }

        if (channel.get().getId() != registrationChannelID){
            respond.thenAccept(res -> res.setContent("This command is restricted to <#%d>".formatted(registrationChannelID)).update());
            return;
        }

        try {

            List<SlashCommandInteractionOption> arguments = slashCommandInteraction.getArguments();
            String clanTag = arguments.get(0).getStringValue().get();
            String divisionAlias = arguments.get(1).getStringValue().get();
            String teamAlias = arguments.get(2).getStringValue().get();
            User user = event.getSlashCommandInteraction().getUser();
            User secondRep = user;
            if (arguments.size() >= 4)
                secondRep = arguments.get(3).getUserValue().orElse(user);
            if (teamAlias.length() > 5) {
                respond.thenApply(x -> x.setContent("Team Alias must be between the length of 1 - 5")).thenApply(x -> x.update());
                return;
            }
            Division division = divisionService.getDivisionByAlias(divisionAlias);
            ClanModel clan = clash.getClan(clanTag).join();

            //Make team
            Team team = new Team(
                    clan.getName(),
                    clan.getTag(),
                    teamAlias,
                    division,
                    user.getId(),
                    secondRep.getId(),
                    division.getAllowedRosterChanges()
            );

            team = teamService.saveTeam(team);
            ServerTextChannel applicantChannel =
                    createApplicantChannel(event.getSlashCommandInteraction().getServer().get(), user, secondRep, team);

            team.setRegistrationChannelID(applicantChannel.getId());

            EmbedBuilder embedBuilder = new EmbedBuilder()
                    .setTitle("Registration successful")
                    .setDescription("Hey there, you have successfully registered for FTHL season 7! \nHere are some commands that might be useful to you ")
                    .addField("Team Name", team.getName(), false)
                    .addField("Clan Tag", team.getTag(), false)
                    .addField("Division", team.getDivision().getName(), false)
                    .addField("Representatives", user.getDiscriminatedName() + "\n" + secondRep.getDiscriminatedName(), false)
                    .addInlineField("commands", "`/team-info`\n`/team-roster`\n`/all-team`")
                    .addInlineField("Roster Management", "`/roster-add`\n`/roster-remove`")
                    .addInlineField("For Additional Help", "`/help`")
                    .setThumbnail("https://media.discordapp.net/attachments/777902179771613184/970270133769633812/fthl-logo.png?width=670&height=670")
                    .setTimestampToNow()
                    .setColor(Color.green)
                    .setAuthor(user);

            //slashCommandInteraction.createImmediateResponder().addEmbeds(embedBuilder).respond();
            User finalSecondRep = secondRep;
            respond.thenAccept(res -> {
                res.setContent("Your application has been recorded. Head over to your private channel to see your application and to manage your roster. <#%d>".formatted(applicantChannel.getId())).update();

                applicantChannel.sendMessage(embedBuilder);
                applicantChannel.sendMessage("<@%d>\n<@%d>".formatted(user.getId(), finalSecondRep.getId()));
            });

            //Log the registration
            EmbedBuilder embed = new EmbedBuilder()
                    .setTitle("New Registration!")
                    .addField("Team Name", team.getName(), true)
                    .addField("Team Alias", team.getAlias(), true)
                    .addField("Clan Tag", team.getTag(), true)
                    .addField("Division", division.getAlias(), true)
                    .addField("Team Resp", "%s\n%s".formatted(user.getDiscriminatedName(), secondRep.getDiscriminatedName()))
                    .setTimestampToNow()
                    .setColor(Color.BLUE);
            event.getApi().getTextChannelById(config.getRegistrationAndRosterLogChannelID()).get().sendMessage(embed);

        } catch (LeagueException e) {
            leagueSlashErrorMessage(respond, e);
            e.printStackTrace();
        } catch (ClashAPIException | IOException e) {
            ClashExceptionHandler handler = new ClashExceptionHandler();
            handler.setResponder(respond.join()).setStatusCode(Integer.valueOf(e.getMessage()));
            handler.respond();
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private boolean isRegistrationOpen() {
        Date registrationDate;
        try {
            registrationDate = config.getRegistrationDate(); //start
            Date leagueStartDate = config.getLeagueStartDate(); //end

            if (registrationDate.after(new Date()))
                return false;
            return !leagueStartDate.before(new Date());
        } catch (ParseException e) {
            e.printStackTrace();
            logger.error("Error parsing registration date");
        }
        return false;
    }

    public ServerTextChannel createApplicantChannel(Server server, User applicant, User applicant2, Team team) {
        ServerTextChannelBuilder textChannelBuilder = server.createTextChannelBuilder();
        //Set channel name to division alias + team name
        textChannelBuilder.setName(team.getDivision().getAlias() + "-" + team.getName());
        //Set channel topic to team id
        textChannelBuilder.setTopic(team.getID().toString());

        PermissionsBuilder everyoneElse = new PermissionsBuilder().setDenied(PermissionType.VIEW_CHANNEL);
        PermissionsBuilder rep = new PermissionsBuilder().setAllowed(PermissionType.VIEW_CHANNEL, PermissionType.SEND_MESSAGES);

        //Give applicant permissions to view channel
        textChannelBuilder.addPermissionOverwrite(applicant, rep.build());
        textChannelBuilder.addPermissionOverwrite(applicant2, rep.build());
        textChannelBuilder.addPermissionOverwrite(server.getEveryoneRole(), everyoneElse.build());

        //Set Category by division alias

        ChannelCategory channelCategory = getChannelCategory(team.getDivision().getAlias(), server);

        textChannelBuilder.setCategory(channelCategory);
        //Create channel
        return textChannelBuilder.create().join();
    }

    //A method that takes a string and returns a ChannelCatrgorie object when the string matches the name of a category
    private ChannelCategory getChannelCategory(String categoryName, Server server) {
        for (ChannelCategory category : server.getChannelCategories()) {
            if (category.getName().equalsIgnoreCase(categoryName)) {
                return category;
            }
        }
        return null;
    }

}
