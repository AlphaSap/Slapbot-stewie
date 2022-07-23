package com.fthlbot.discordbotfthl.Commands.CommandImpl.LeagueCommandsImpl;

import Core.Enitiy.player.Player;
import Core.JClash;
import Core.exception.ClashAPIException;
import com.fthlbot.discordbotfthl.DatabaseModels.Exception.LeagueException;
import com.fthlbot.discordbotfthl.core.Annotation.CommandType;
import com.fthlbot.discordbotfthl.core.Annotation.Invoker;
import com.fthlbot.discordbotfthl.DatabaseModels.Division.Division;
import com.fthlbot.discordbotfthl.DatabaseModels.Division.DivisionService;
import com.fthlbot.discordbotfthl.DatabaseModels.Exception.EntityNotFoundException;
import com.fthlbot.discordbotfthl.DatabaseModels.Roster.Roster;
import com.fthlbot.discordbotfthl.DatabaseModels.Roster.RosterService;
import com.fthlbot.discordbotfthl.DatabaseModels.Team.Team;
import com.fthlbot.discordbotfthl.DatabaseModels.Team.TeamService;
import com.fthlbot.discordbotfthl.core.Handlers.Command;
import com.fthlbot.discordbotfthl.Util.BotConfig;
import com.fthlbot.discordbotfthl.Util.Exception.ClashExceptionHandler;
import com.fthlbot.discordbotfthl.Util.GeneralService;
import org.javacord.api.entity.message.embed.EmbedBuilder;
import org.javacord.api.event.interaction.SlashCommandCreateEvent;
import org.javacord.api.interaction.callback.InteractionOriginalResponseUpdater;
import org.springframework.stereotype.Component;

import java.awt.*;
import java.io.IOException;
import java.util.concurrent.CompletableFuture;

@Component
@Invoker(
        alias = "roster-remove",
        description = "A command to remove accounts from your master roster. Add multiple tags seperated by tags",
        usage = "/roster-remove <DIVISION ALIAS> <TEAM ALIAS> <TAGs ...>",
        type = CommandType.ROSTER_MANAGEMENT
)
public class RosterRemove implements Command {
    private final TeamService teamService;
    private final RosterService rosterService;

    private final DivisionService divisionService;

    private final BotConfig config;
    public RosterRemove(TeamService teamService, RosterService rosterService, DivisionService divisionService, BotConfig config) {
        this.teamService = teamService;
        this.rosterService = rosterService;
        this.divisionService = divisionService;
        this.config = config;
    }

    @Override
    public void execute(SlashCommandCreateEvent event) {
        CompletableFuture<InteractionOriginalResponseUpdater> respondLater = event.getSlashCommandInteraction().respondLater();
        String divisionAlias = event.getSlashCommandInteraction().getArguments().get(0).getStringValue().get();
        String teamAlias = event.getSlashCommandInteraction().getArguments().get(1).getStringValue().get();
        String[] tags = event.getSlashCommandInteraction().getArguments().get(2).getStringValue().get().split("\\s+");

        Division division;
        Team team;
        try {
            division = divisionService.getDivisionByAlias(divisionAlias);
            team = teamService.getTeamByDivisionAndAlias(teamAlias, division);
        } catch (EntityNotFoundException e) {
            GeneralService.getLeagueError(e, event);
            return;
        }
        respondLater.thenAccept(res -> {
            res.setContent("Removing " + tags.length + " accounts from " + team.getName() + "...");
            res.update();
        });
        JClash jClash = new JClash();
        for (String tag : tags) {
            try {
                Player join = jClash.getPlayer(tag).join();
                Roster roster = rosterService.removeFromRoster(team, join.getTag(), event.getSlashCommandInteraction().getUser());
                EmbedBuilder embedBuilder = new EmbedBuilder()
                        .setTitle("Removed " + join.getName() + " from " + team.getName())
                        .setTimestampToNow()
                        .setDescription("Tag: " + join.getTag() + "\n" +
                                "Name: " + join.getName() + "\n" +
                                "Level: " + join.getTownHallLevel()
                        ).setColor(Color.GREEN).setAuthor(event.getSlashCommandInteraction().getUser());
                event.getSlashCommandInteraction().createFollowupMessageBuilder().addEmbed(embedBuilder).send();

                CompletableFuture.runAsync(() -> {
                   EmbedBuilder embed = new EmbedBuilder()
                           .addField("Team Name", team.getName())
                           .addField("Division", team.getDivision().getAlias())
                           .addField("Player Tag", roster.getPlayerTag())
                           .addField("Player Name", roster.getPlayerName())
                           .setColor(Color.RED)
                           .setTimestampToNow();
                    event.getApi().getTextChannelById(config.getRegistrationAndRosterLogChannelID()).get().sendMessage(embed);

                });
            } catch (IOException e) {
                EmbedBuilder embedBuilder = new EmbedBuilder()
                        .setTitle("Failed to remove " + tag + " from " + team.getName())
                        .setTimestampToNow()
                        .addField("Reason", e.getMessage(), false)
                        .setFooter("Contact the developer if this persists")
                        .setDescription("Tag: " + tag + "\n" +
                                "Name: " + tag + "\n" +
                                "Level: " + tag
                        ).setColor(Color.RED).setAuthor(event.getSlashCommandInteraction().getUser());
                event.getSlashCommandInteraction().createFollowupMessageBuilder().addEmbed(embedBuilder).send();

            }catch (ClashAPIException e) {
                ClashExceptionHandler clashExceptionHandler = new ClashExceptionHandler();
                clashExceptionHandler.setStatusCode(Integer.valueOf(e.getMessage()));
                EmbedBuilder embedBuilder = clashExceptionHandler.createEmbed(tag).getEmbedBuilder();
                event.getSlashCommandInteraction().createFollowupMessageBuilder().addEmbed(embedBuilder).send();
            } catch (LeagueException e) {
                EmbedBuilder embedBuilder = GeneralService.getLeagueError(e, event);
                event.getSlashCommandInteraction().createFollowupMessageBuilder().addEmbed(embedBuilder).send();
            }
        }
    }
}
