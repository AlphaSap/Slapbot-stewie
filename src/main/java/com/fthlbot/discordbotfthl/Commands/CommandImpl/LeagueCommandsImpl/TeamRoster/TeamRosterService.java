package com.fthlbot.discordbotfthl.Commands.CommandImpl.LeagueCommandsImpl.TeamRoster;

import com.fthlbot.discordbotfthl.DatabaseModels.Division.Division;
import com.fthlbot.discordbotfthl.DatabaseModels.Division.DivisionService;
import com.fthlbot.discordbotfthl.DatabaseModels.Exception.EntityNotFoundException;
import com.fthlbot.discordbotfthl.DatabaseModels.Roster.Roster;
import com.fthlbot.discordbotfthl.DatabaseModels.Roster.RosterService;
import com.fthlbot.discordbotfthl.DatabaseModels.Team.Team;
import com.fthlbot.discordbotfthl.DatabaseModels.Team.TeamService;
import com.fthlbot.discordbotfthl.Util.GeneralService;
import org.javacord.api.entity.message.embed.EmbedBuilder;
import org.javacord.api.event.interaction.SlashCommandCreateEvent;
import org.javacord.api.interaction.SlashCommandInteraction;
import org.javacord.api.interaction.callback.InteractionOriginalResponseUpdater;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class TeamRosterService {
    public void execute(SlashCommandCreateEvent event, TeamService teamService, DivisionService divisionService, RosterService rosterService, long time){
            SlashCommandInteraction interaction = event.getSlashCommandInteraction();
            CompletableFuture<InteractionOriginalResponseUpdater> response = interaction.respondLater();
        try{
            String divAlias = interaction.getArguments().get(0).getStringValue().get();
            String teamAlias = interaction.getArguments().get(1).getStringValue().get();


            Division division = divisionService.getDivisionByAlias(divAlias);
            Team team = teamService.getTeamByDivisionAndAlias(teamAlias, division);

            List<Roster> rosterByTeam = rosterService.getRosterForATeam(team);

            if (rosterByTeam.size() == 0) {
                //Make a warning embedBuilder
                EmbedBuilder embedBuilder = GeneralService.getEmbedBuilder(Color.YELLOW,  "No roster found for team `" + team.getName() + "` in division " + division.getName());
                response.thenAccept(res -> {
                   res.addEmbed(embedBuilder).update();
                });
                return;
            }

            List<List<String>> formattedTeams = getFormattedTeams(rosterByTeam);
            EmbedBuilder embedBuilder = new EmbedBuilder()
                    .setTitle("Roster for: " + team.getName())
                    .setColor(Color.cyan)
                    .setTimestampToNow()
                    .setAuthor(interaction.getUser());
            addField(embedBuilder, formattedTeams);
            embedBuilder.setFooter(String.valueOf((double) (System.nanoTime() - time) / 1000000000));
            response.thenAccept(res -> res.addEmbed(embedBuilder).update());

        } catch (EntityNotFoundException e) {
            e.printStackTrace();
            GeneralService.leagueSlashErrorMessage(response, e);
        }catch (Exception e){
            e.printStackTrace();
            GeneralService.leagueSlashErrorMessage(response, "expected error!");
        }
    }

    private List<List<String>> getFormattedTeams(List<Roster> rosters){
        List<List<String>> strTeam = new ArrayList<>();
        int currentList = 0;
        int lineCount = 0;
        strTeam.add(new ArrayList<>());

        for(Roster roster: rosters){
            String tag = roster.getPlayerTag();
            String name = roster.getPlayerName();
            String alias  = String.valueOf(roster.getTownHallLevel().intValue());
            String id = String.valueOf(roster.getID());

            String toAdd = formatRow(id, tag, alias, name, "");

            for (int i = 0; Math.ceil((double) lineCount / 900) > strTeam.size(); i++) {
                strTeam.add(new ArrayList<String>());
                currentList++;
            }
            strTeam.get(currentList).add(toAdd);
        }
        return strTeam;
    }
    private void addField(EmbedBuilder em, List<List<String>> list) {
        for (List<String> roster : list) {
            String description = String.join("\n", roster);
            em.addField(formatRow("ID" ,"Tag", "Alias", "Name", "   "), "```" + description + "```");
        }
    }
    final static int ID_MAX_LEN = 11,  ALIAS_MAX_LEN = 5, NAME_MAX_LEN = 2, MAXID = 4;

    private static String formatRow(String id, String name, String tag, String alias, String ext) {
        return String.format("%-"+(MAXID + ext.length())+"s%-" + (ID_MAX_LEN + ext.length()) + "s%-" + (ALIAS_MAX_LEN + ext.length()) +
                "s%-" + (NAME_MAX_LEN) + "s",id, name + ext, tag + ext, alias);
    }
}
