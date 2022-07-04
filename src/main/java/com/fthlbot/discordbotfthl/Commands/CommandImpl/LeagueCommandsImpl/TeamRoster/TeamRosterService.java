package com.fthlbot.discordbotfthl.Commands.CommandImpl.LeagueCommandsImpl.TeamRoster;

import com.fthlbot.discordbotfthl.DatabaseModels.Division.Division;
import com.fthlbot.discordbotfthl.DatabaseModels.Division.DivisionService;
import com.fthlbot.discordbotfthl.DatabaseModels.Exception.EntityNotFoundException;
import com.fthlbot.discordbotfthl.DatabaseModels.Roster.Roster;
import com.fthlbot.discordbotfthl.DatabaseModels.Roster.RosterService;
import com.fthlbot.discordbotfthl.DatabaseModels.Team.Team;
import com.fthlbot.discordbotfthl.DatabaseModels.Team.TeamService;
import com.fthlbot.discordbotfthl.Util.GeneralService;
import com.fthlbot.discordbotfthl.Util.Pagination.Pagination;
import org.javacord.api.entity.message.embed.EmbedBuilder;
import org.javacord.api.event.interaction.SlashCommandCreateEvent;
import org.javacord.api.interaction.SlashCommandInteraction;
import org.javacord.api.interaction.callback.InteractionOriginalResponseUpdater;
import org.javacord.api.util.logging.ExceptionLogger;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.*;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class TeamRosterService {

    private static final Logger log = LoggerFactory.getLogger(TeamRosterService.class);

    public void execute(SlashCommandCreateEvent event, TeamService teamService, DivisionService divisionService, RosterService rosterService, long time) {
        SlashCommandInteraction interaction = event.getSlashCommandInteraction();
        CompletableFuture<InteractionOriginalResponseUpdater> response = interaction.respondLater();
        try {
            String divAlias = interaction.getArguments().get(0).getStringValue().get();
            String teamAlias = interaction.getArguments().get(1).getStringValue().get();
            log.info("TeamRosterService: divAlias: " + divAlias + " teamAlias: " + teamAlias);

            Division division = divisionService.getDivisionByAlias(divAlias);
            Team team = teamService.getTeamByDivisionAndAlias(teamAlias, division);

            List<Roster> rosterByTeam = rosterService.getRosterForATeam(team);

            rosterByTeam.sort(Comparator.comparingInt(Roster::getTownHallLevel));

            log.info("TeamRosterService: rosterByTeam: " + rosterByTeam);
            if (rosterByTeam.size() == 0) {
                //Make a warning embedBuilder
                EmbedBuilder embedBuilder = GeneralService.getEmbedBuilder(Color.YELLOW, "No roster found for team `" + team.getName() + "` in division " + division.getName());
                response.thenAccept(res -> {
                    res.addEmbed(embedBuilder).update();
                }).exceptionally(ExceptionLogger.get());
                log.info("TeamRosterService: No roster found for team `" + team.getName() + "` in division " + division.getName());
                return;
            }
            List<EmbedBuilder> embedBuilders = new ArrayList<>();

            List<StringBuilder> s = new ArrayList<>();
            int count = 0;
            //write an algorithm that appends a string in a list of strings, for every 15th player in rosterByTeam
            for (int i = 0; i < rosterByTeam.size(); i++) {
                if (i % 15 == 0) {
                    s.add(new StringBuilder());
                }
                //s.get(i / 15).append(rosterByTeam.get(i).getPlayerName()).append("\n");
                s.get(i / 15)
                        .append(formatRow(rosterByTeam.get(i).getPlayerTag(), rosterByTeam.get(i).getPlayerName(), rosterByTeam.get(i).getTownHallLevel().toString()))
                        .append("\n");

            }

            for (int i = 0; i < s.size(); i++) {
                StringBuilder stringBuilder = s.get(i);
                embedBuilders.add(new EmbedBuilder()
                        .setTitle("Team Roster")
                        .addField("Team", team.getName(), false)
                        .addField("Division", division.getName(), false)
                        .addField("Roster", "```" + stringBuilder.append("```").toString(), false)
                        .setTimestampToNow()
                        .setColor(Color.GREEN)
                        .setFooter("Page " + (i + 1) + " of " + s.size())
                );
            }

            new Pagination().buttonPagination(embedBuilders, response, event.getApi());

        } catch (EntityNotFoundException e) {
            e.printStackTrace();
            GeneralService.leagueSlashErrorMessage(response, e);
        } catch (Exception e) {
            e.printStackTrace();
            GeneralService.leagueSlashErrorMessage(response, "expected error!");
        }
    }
    final static int TAG_MAX_LEN = 11,  TH_MAX_LEN = 2, NAME_MAX_LEN = 15, MAXID = 4;

    private static String formatRow(String tag, String name, String townHallLevel) {
        return String.format("%-" + (3+TAG_MAX_LEN) + "s%-" + (TH_MAX_LEN+3) + "s%-" + (NAME_MAX_LEN) + "s", tag, townHallLevel, name);
    }

}
