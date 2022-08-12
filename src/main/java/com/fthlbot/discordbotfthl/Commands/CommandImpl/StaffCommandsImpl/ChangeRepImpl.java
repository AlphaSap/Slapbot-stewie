package com.fthlbot.discordbotfthl.Commands.CommandImpl.StaffCommandsImpl;

import com.fthlbot.discordbotfthl.DatabaseModels.Exception.LeagueException;
import com.fthlbot.discordbotfthl.core.Annotation.CommandType;
import com.fthlbot.discordbotfthl.core.Annotation.Invoker;
import com.fthlbot.discordbotfthl.Commands.CommandListener.StaffCommandListener.ChangeRepListener;
import com.fthlbot.discordbotfthl.DatabaseModels.Division.Division;
import com.fthlbot.discordbotfthl.DatabaseModels.Division.DivisionService;
import com.fthlbot.discordbotfthl.DatabaseModels.Exception.EntityNotFoundException;
import com.fthlbot.discordbotfthl.DatabaseModels.Exception.NotTheRepException;
import com.fthlbot.discordbotfthl.DatabaseModels.Team.Team;
import com.fthlbot.discordbotfthl.DatabaseModels.Team.TeamService;
import com.fthlbot.discordbotfthl.Util.BotConfig;
import com.fthlbot.discordbotfthl.Util.GeneralService;
import org.javacord.api.entity.message.embed.EmbedBuilder;
import org.javacord.api.entity.user.User;
import org.javacord.api.event.interaction.SlashCommandCreateEvent;
import org.javacord.api.interaction.SlashCommandInteraction;
import org.javacord.api.interaction.callback.InteractionOriginalResponseUpdater;
import org.javacord.api.util.logging.ExceptionLogger;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.awt.*;
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
        try {
            division = divisionService.getDivisionByAlias(divAlias);
            log.info(division.getName());
        } catch (EntityNotFoundException e) {
            GeneralService.leagueSlashErrorMessage(respondLater, e);
            e.printStackTrace();
            return;
        }
        Team team = null;
        try {
             team = teamService.getTeamByDivisionAndAlias(teamAlias, division);
             log.info(team.getName());
        } catch (EntityNotFoundException e) {
            GeneralService.leagueSlashErrorMessage(respondLater, e);
            e.printStackTrace();
            return;
        }
        try {
            team = teamService.changeRep(newRep, oldRep, team);
            log.info(team.getRep1ID() + "");
            log.info(team.getRep2ID()+  "");
        } catch (LeagueException e) {
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
            res.addEmbed(embedBuilder).update();
        }).exceptionally(ExceptionLogger.get());
    }


}
