package com.fthlbot.discordbotfthl.Commands.CommandImpl;

import Core.Enitiy.clan.ClanModel;
import Core.JClash;
import Core.exception.ClashAPIException;
import com.fthlbot.discordbotfthl.Annotation.CommandType;
import com.fthlbot.discordbotfthl.Annotation.Invoker;
import com.fthlbot.discordbotfthl.Commands.CommandListener.RegistrationListener;
import com.fthlbot.discordbotfthl.DatabaseModels.Division.Division;
import com.fthlbot.discordbotfthl.DatabaseModels.Division.DivisionService;
import com.fthlbot.discordbotfthl.DatabaseModels.Exception.LeagueException;
import com.fthlbot.discordbotfthl.DatabaseModels.Team.Team;
import com.fthlbot.discordbotfthl.DatabaseModels.Team.TeamService;
import com.fthlbot.discordbotfthl.Util.Exception.ClashExceptionHandler;
import com.fthlbot.discordbotfthl.Util.GeneralService;
import org.javacord.api.entity.message.embed.EmbedBuilder;
import org.javacord.api.entity.user.User;
import org.javacord.api.event.interaction.SlashCommandCreateEvent;
import org.javacord.api.event.message.MessageCreateEvent;
import org.javacord.api.interaction.SlashCommandInteraction;
import org.javacord.api.interaction.SlashCommandInteractionOption;
import org.javacord.api.interaction.callback.InteractionCallbackDataFlag;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.awt.*;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import static com.fthlbot.discordbotfthl.DiscordBotFthlApplication.clash;
import static com.fthlbot.discordbotfthl.Util.GeneralService.*;

@Component
@Invoker(
        alias = "register",
        description = "A simple registration command!",
        usage = "/register <CLAN TAG> <DIVISION ALIAS> <TEAM ALIAS> <@Second Rep (optional)>",
        type = CommandType.REGISTRATION
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

//TODO ADD THE PREDICT MESSAGE DELETE THING FROM THE MAIN PROJECT

//Make slash commands, /register <clan tag> <div alias> <team alias> <optional @second rep>
public class RegistrationImpl implements RegistrationListener {
    @Autowired
    private DivisionService divisionService;
    @Autowired
    private TeamService teamService;

    @Deprecated(since = "2022/March/12")
    public void execute1(MessageCreateEvent event) {
        try {

            String[] args = event.getMessageContent().split("\\s+");
            JClash clash = new JClash();
            String tag = args[1];
            String divAlias = args[2];
            String teamAlias = args[3];

            User firstRep = event.getMessageAuthor().asUser().get();

            User secondRep = event.getMessage().getMentionedUsers().isEmpty() ?
                    event.getMessageAuthor().asUser().get() :
                    event.getMessage().getMentionedUsers().get(0);

            Division division = divisionService.getDivisionByAlias(divAlias);
            ClanModel clan = clash.getClan(tag);

            //Make team
            Team team = new Team(
                    clan.getName(),
                    clan.getTag(),
                    teamAlias,
                    division,
                    firstRep.getId(),
                    secondRep.getId(),
                    division.getAllowedRosterChanges()
            );

            teamService.saveTeam(team);


            EmbedBuilder embedBuilder = new EmbedBuilder()
                    .setTitle("Registration successful")
                    .setDescription("Hey there, you have successfully registered for FTHL season 6! \nHere are some commands that might be useful to you ")
                    .addInlineField("commands", "+team mr\n+team info\n+team all")
                    .addInlineField("Roster Management", "+add\n+remove" )
                    .setTimestampToNow()
                    .setColor(Color.green)
                    .setAuthor(event.getMessageAuthor());
            event.getChannel().sendMessage(embedBuilder);
            //TODO make channel


        } catch (IndexOutOfBoundsException e) {
            EmbedBuilder embedBuilder = GeneralService.notEnoughArgument();
            event.getChannel().sendMessage(embedBuilder);
        } catch (ClashAPIException | IOException e) {
            e.printStackTrace();
        }catch (LeagueException e){
            getLeagueError(e, event);
            e.printStackTrace();
        }


    }

    @Override
    public void execute(SlashCommandCreateEvent event) {
        try{
        SlashCommandInteraction slashCommandInteraction = event.getSlashCommandInteraction();
        //Return if channel is not the same as reg channel
            //TODO
           /* if (!isRegChannel(event.getSlashCommandInteraction().getChannel().get().getCommandID())){
                slashCommandInteraction.createImmediateResponder()
                        .setContent("This command can only be run the registration channel in the applicant server, please join the applicant server and run this command!")
                        .setFlags(InteractionCallbackDataFlag.EPHEMERAL)
                        .respond();
                return;
            }*/
        List<SlashCommandInteractionOption> arguments = slashCommandInteraction.getArguments();
        String clanTag = arguments.get(0).getStringValue().get();
        String divisionAlias = arguments.get(1).getStringValue().get();
        String teamAlias = arguments.get(2).getStringValue().get();
        User user = event.getSlashCommandInteraction().getUser();
        User secondRep = user;
        if (arguments.size() >= 4)
            secondRep = arguments.get(3).getUserValue().orElse(user);

        Division division = divisionService.getDivisionByAlias(divisionAlias);
        ClanModel clan = clash.getClan(clanTag);

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

        teamService.saveTeam(team);


        EmbedBuilder embedBuilder = new EmbedBuilder()
                .setTitle("Registration successful")
                .setDescription("Hey there, you have successfully registered for FTHL season 6! \nHere are some commands that might be useful to you ")
                .addInlineField("commands", "+team mr\n+team info\n+team all")
                .addInlineField("Roster Management", "+add\n+remove")
                .setTimestampToNow()
                .setColor(Color.green)
                .setAuthor(user);

        slashCommandInteraction.createImmediateResponder().addEmbeds(embedBuilder).respond();
        /*slashCommandInteraction.respondLater().thenAccept(res -> {
            res.addEmbed(embedBuilder);
            res.update();
        });*/

        }catch(LeagueException e){
            leagueSlashErrorMessage(event, e);
            e.printStackTrace();
        }catch (ClashAPIException | IOException e){
            ClashExceptionHandler handler = new ClashExceptionHandler();
            handler.setSlashCommandCreateEvent(event)
                    .setStatusCode(Integer.valueOf(e.getMessage()));
            handler.respond();
            e.printStackTrace();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private boolean isRegChannel(long channelID) throws IOException {
        String content = getFileContent("Channels.json");

        JSONObject jsonObject = new JSONObject(content);

        long registrationChannel = jsonObject.getLong("registrationChannel");

        return channelID == registrationChannel;
    }


}
