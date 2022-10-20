package com.fthlbot.discordbotfthl.Commands.CommandImpl.StaffCommandsImpl;

import com.fthlbot.discordbotfthl.Commands.CommandImpl.CommandException.ChangeRepCommandException;
import com.fthlbot.discordbotfthl.Commands.CommandImpl.CommandException.CommandException;
import com.fthlbot.discordbotfthl.Commands.CommandListener.StaffCommandListener.ChangeRepListener;
import com.fthlbot.discordbotfthl.DatabaseModels.Division.Division;
import com.fthlbot.discordbotfthl.DatabaseModels.Division.DivisionService;
import com.fthlbot.discordbotfthl.DatabaseModels.Exception.LeagueException;
import com.fthlbot.discordbotfthl.DatabaseModels.Team.Team;
import com.fthlbot.discordbotfthl.DatabaseModels.Team.TeamService;
import com.fthlbot.discordbotfthl.Util.BotConfig;
import com.fthlbot.discordbotfthl.Util.GeneralService;
import com.fthlbot.discordbotfthl.core.Annotation.CommandType;
import com.fthlbot.discordbotfthl.core.Annotation.Invoker;
import org.javacord.api.DiscordApi;
import org.javacord.api.entity.channel.ServerChannel;
import org.javacord.api.entity.channel.ServerTextChannel;
import org.javacord.api.entity.message.embed.EmbedBuilder;
import org.javacord.api.entity.permission.PermissionType;
import org.javacord.api.entity.permission.PermissionsBuilder;
import org.javacord.api.entity.server.Server;
import org.javacord.api.entity.user.User;
import org.javacord.api.event.interaction.SlashCommandCreateEvent;
import org.javacord.api.interaction.SlashCommandInteraction;
import org.javacord.api.interaction.callback.InteractionOriginalResponseUpdater;
import org.javacord.api.util.logging.ExceptionLogger;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

@Invoker(
        alias = "change-rep",
        usage = "/change-rep <DIVISION> <TEAM ALIAS> <@oldRep> <@newRep>",
        description = "Changes representative for a team. Can only be used by staff",
        type = CommandType.STAFF
)
@Component
public class ChangeRepImpl implements ChangeRepListener {
    private final DivisionService divisionService;
    private final TeamService teamService;
    private final BotConfig config;
    private final Logger log = LoggerFactory.getLogger(ChangeRepImpl.class);

    public ChangeRepImpl(DivisionService divisionService, TeamService teamService, BotConfig config) {
        this.divisionService = divisionService;
        this.teamService = teamService;
        this.config = config;
    }

    @Override
    public void execute(SlashCommandCreateEvent event) {
        SlashCommandInteraction slashCommandInteraction = event.getSlashCommandInteraction();
        CompletableFuture<InteractionOriginalResponseUpdater> respondLater = slashCommandInteraction.respondLater();
        User user = slashCommandInteraction.getUser();

        String divAlias = slashCommandInteraction.getArguments().get(0).getStringValue().get();
        String teamAlias = slashCommandInteraction.getArguments().get(1).getStringValue().get();
        User oldRep = slashCommandInteraction.getArguments().get(2).getUserValue().get();
        User newRep = slashCommandInteraction.getArguments().get(3).getUserValue().get();
        Division division = null;
        Team team = null;
        List<EmbedBuilder> emb = new ArrayList<>();
        try {
            division = divisionService.getDivisionByAlias(divAlias);
            team = teamService.getTeamByDivisionAndAlias(teamAlias, division);
            team = teamService.changeRep(newRep, oldRep, team);
            changeChannelPermissionAndAddUser(team, newRep, event.getApi());
        } catch (CommandException e) {
            emb.add(GeneralService.warnSlashErrorMessageAsEmbed(e));
        }catch (LeagueException e) {
            GeneralService.leagueSlashErrorMessage(respondLater, e);
            return;
        }


        EmbedBuilder embedBuilder = new EmbedBuilder()
                .setTitle("Rep change successfully executed!<:check:934403622043783228>")
                .addInlineField("Clan name", team.getName())
                .addInlineField("Clan tag", team.getTag())
                .addInlineField("Old rep", oldRep.getDiscriminatedName())
                .addInlineField("New Rep", newRep.getDiscriminatedName())
                .addInlineField("Change approved by", user.getDiscriminatedName())
                .setTimestampToNow()
                .setColor(Color.CYAN)
                .setAuthor(user);

        respondLater.thenAccept(res -> {
            res.addEmbed(embedBuilder);

            for (EmbedBuilder builder : emb) {
                res.addEmbed(builder);
            }
            res.update();
        }).exceptionally(ExceptionLogger.get());
    }

    private void changeChannelPermissionAndAddUser(Team team, User newRep, DiscordApi api) throws ChangeRepCommandException {
        Optional<Server> appServerOpt = api.getServerById(config.getApplicantServerID());
        if (appServerOpt.isEmpty()){
            throw new ChangeRepCommandException(
                    """
                    Applicant Server is not avaliabe to the Bot.
                    """
            );
        }
        Server appServer = appServerOpt.get();

        boolean member = appServer.isMember(newRep);
        if (!member) {
            throw new ChangeRepCommandException(
                    """
                    %s is not present in the application server!
                    """.formatted(newRep.getDiscriminatedName())
            );
        }
        if (team.getRegistrationChannelID().isEmpty()){
            throw new ChangeRepCommandException(
                    """
                    Unable to find the registration channel ID %s, a Staff member has to manually add the new rep to their application channel!
                    """.formatted(team.getName())
            );
        }

        Optional<ServerChannel> channelById = appServer.getChannelById(team.getRegistrationChannelID().get());
        if (channelById.isEmpty()) {
            throw new ChangeRepCommandException(
                    """
                    Channel with the ID %d not found!
                    """.formatted(team.getRegistrationChannelID().get())
            );
        }

        Optional<ServerTextChannel> textChannel = channelById.get().asServerTextChannel();
        if (textChannel.isEmpty()) {
            throw new ChangeRepCommandException(
                    """
                    Channel with the ID %d is not a Text channel.
                    """.formatted(team.getRegistrationChannelID().get())
            );
        }

        boolean b = textChannel.get().hasPermissions(newRep, PermissionType.VIEW_CHANNEL, PermissionType.SEND_MESSAGES);
        if (b) {
            return;
        }
        PermissionsBuilder rep = new PermissionsBuilder().setAllowed(PermissionType.VIEW_CHANNEL, PermissionType.SEND_MESSAGES);

        textChannel.get().createUpdater().addPermissionOverwrite(newRep, rep.build());
    }
}
