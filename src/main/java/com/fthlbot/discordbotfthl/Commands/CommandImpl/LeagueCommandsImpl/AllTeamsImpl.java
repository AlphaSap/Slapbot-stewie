package com.fthlbot.discordbotfthl.Commands.CommandImpl.LeagueCommandsImpl;

import com.fthlbot.discordbotfthl.core.Annotation.CommandType;
import com.fthlbot.discordbotfthl.core.Annotation.Invoker;
import com.fthlbot.discordbotfthl.Commands.CommandListener.AllTeamsListener;
import com.fthlbot.discordbotfthl.DatabaseModels.Division.Division;
import com.fthlbot.discordbotfthl.DatabaseModels.Division.DivisionService;
import com.fthlbot.discordbotfthl.DatabaseModels.Exception.EntityNotFoundException;
import com.fthlbot.discordbotfthl.DatabaseModels.Team.Team;
import com.fthlbot.discordbotfthl.DatabaseModels.Team.TeamService;
import com.fthlbot.discordbotfthl.Util.GeneralService;
import com.fthlbot.discordbotfthl.Util.Pagination.Pagination;
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

        try {
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
            if (allTeamsByDivision.isEmpty()) {
                EmbedBuilder embedBuilder;
                embedBuilder = new EmbedBuilder()
                        .setTimestampToNow()
                        .setDescription("No teams have registered for this division yet!")
                        .setColor(Color.YELLOW)
                        .setAuthor(event.getSlashCommandInteraction().getUser());

                respondLater.thenAccept(res -> {
                    res.addEmbed(embedBuilder);
                    res.update();
                }).exceptionally(ExceptionLogger.get());
                return;
            }

            List<StringBuilder> sb = new ArrayList<>();
            List<EmbedBuilder> embedBuilders = new ArrayList<>();

            int count = 0;
            //write an algorithm that appends a string in a list of strings, for every 15th player in rosterByTeam
            for (int i = 0; i < allTeamsByDivision.size(); i++) {
                if (i % 15 == 0) {
                    sb.add(new StringBuilder());
                }
                sb.get(i / 15)
                        //.append(formatRow("ID", "Clan Tag", "Alias", "Team Name", "  "))
                        .append(
                                formatRow(allTeamsByDivision.get(i).getID().toString(),
                                allTeamsByDivision.get(i).getTag(),
                                allTeamsByDivision.get(i).getAlias(),
                                allTeamsByDivision.get(i).getName(),
                                "  ")
                        )
                        .append("\n");

            }

            for (int i = 0; i < sb.size(); i++) {
                StringBuilder stringBuilder = sb.get(i);
                embedBuilders.add(new EmbedBuilder()
                        .setTitle("Team Roster")
                        .addField("Division", divisionByAlias.getName(), false)
                        .addField("Registered Teams", allTeamsByDivision.size() + "", false)
                        .addField(formatRow("ID", "Clan Tag", "Alias", "Team Name", "  "),
                                "```" + stringBuilder.append("```"),
                                false
                        )
                        .setTimestampToNow()
                        .setColor(Color.GREEN)
                        .setFooter("Page " + (i + 1) + " of " + sb.size())
                );
            }
            new Pagination().buttonPagination(embedBuilders, respondLater, event.getApi());

        }catch (Exception e) {
            GeneralService.leagueSlashErrorMessage(respondLater, e.getMessage());
            e.printStackTrace();
        }
    }

    @Deprecated
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

    @Deprecated
    private void addField(EmbedBuilder em, List<List<String>> list) {
        for (List<String> roster : list) {
            String description = String.join("\n", roster);
            em.addField(formatRow("ID" ,"Tag", "Alias", "Name", "   "), "```" + description + "```");
        }
    }
    final static int ID_MAX_LEN = 11,  ALIAS_MAX_LEN = 5, NAME_MAX_LEN = 2, MAXID = 6;

    private static String formatRow(String id, String name, String tag, String alias, String ext) {
        return String.format("%-"+(MAXID + ext.length())+"s%-" + (ID_MAX_LEN + ext.length()) + "s%-" + (ALIAS_MAX_LEN + ext.length()) +
                "s%-" + (NAME_MAX_LEN) + "s",id, name + ext, tag + ext, alias);
    }
}
