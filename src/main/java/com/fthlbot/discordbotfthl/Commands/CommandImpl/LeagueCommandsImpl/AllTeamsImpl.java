package com.fthlbot.discordbotfthl.Commands.CommandImpl.LeagueCommandsImpl;

import com.fthlbot.discordbotfthl.Annotation.CommandType;
import com.fthlbot.discordbotfthl.Annotation.Invoker;
import com.fthlbot.discordbotfthl.Commands.CommandListener.AllTeamsListener;
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
import org.javacord.api.interaction.callback.InteractionOriginalResponseUpdater;
import org.javacord.api.util.logging.ExceptionLogger;
import org.springframework.stereotype.Component;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

@Component
@Invoker(
        alias = "all-teams",
        usage = "/all-teams <DIVISION>",
        description = "Returns a list of all the registered teams within a specific division",
        type = CommandType.REGISTRATION
)
public class AllTeamsImpl implements AllTeamsListener {
    private final DivisionService divisionService;
    private final TeamService teamService;
    public AllTeamsImpl(DivisionService divisionService, TeamService teamService) {
        this.divisionService = divisionService;
        this.teamService = teamService;
    }

    @Override
    public void execute(SlashCommandCreateEvent event) {
        CompletableFuture<InteractionOriginalResponseUpdater> respondLater = event.getSlashCommandInteraction().respondLater();

        String div = event.getSlashCommandInteraction().getArguments().get(0).getStringValue().get();
        Division divisionByAlias;
        try {
            divisionByAlias = divisionService.getDivisionByAlias(div);
        } catch (EntityNotFoundException e) {
            GeneralService.leagueSlashErrorMessage(respondLater, e);
            e.printStackTrace();
            return;
        }

        List<Team> allTeamsByDivision = teamService.getAllTeamsByDivision(divisionByAlias);
        EmbedBuilder embedBuilder;
        if (allTeamsByDivision.isEmpty()){
            embedBuilder = new EmbedBuilder()
                    .setTimestampToNow()
                    .setDescription("No teams have registered for this division yet!")
                    .setColor(Color.YELLOW)
                    .setAuthor(event.getSlashCommandInteraction().getUser());
        }else {
            List<List<String>> formattedTeams = getFormattedTeams(allTeamsByDivision);
            embedBuilder = new EmbedBuilder()
                    .setTitle(String.format("Teams in %s", divisionByAlias.getAlias()))
                    .setAuthor(event.getSlashCommandInteraction().getUser())
                    .setTimestampToNow()
                    .setColor(Color.cyan);
            addField(embedBuilder, formattedTeams);
        }
        respondLater.thenAccept(res -> {
           res.addEmbed(embedBuilder);
           res.update();
        }).exceptionally(ExceptionLogger.get());

    }

    private List<List<String>> getFormattedTeams(List<Team> teams){
        List<List<String>> strTeam = new ArrayList<>();
        int currentList = 0;
        int lineCount = 0;
        strTeam.add(new ArrayList<>());

        for(Team team: teams){
            String tag = team.getTag();
            String name = team.getName();
            String alias  = team.getAlias();
            String id = String.valueOf(team.getID());

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
            em.addField(formatRow("ID" ,"Tag", "TownHall", "Name", "   "), "```" + description + "```");
        }
    }
    final static int ID_MAX_LEN = 11,  ALIAS_MAX_LEN = 5, NAME_MAX_LEN = 2, MAXID = 4;

    private static String formatRow(String id, String name, String tag, String alias, String ext) {
        return String.format("%-"+(MAXID + ext.length())+"s%-" + (ID_MAX_LEN + ext.length()) + "s%-" + (ALIAS_MAX_LEN + ext.length()) +
                "s%-" + (NAME_MAX_LEN) + "s",id, name + ext, tag + ext, alias);
    }
}
