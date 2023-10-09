package com.fthlbot.discordbotfthl.Commands.CommandImpl.StaffCommandsImpl;

import com.fthlbot.discordbotfthl.DatabaseModels.Division.Division;
import com.fthlbot.discordbotfthl.DatabaseModels.Division.DivisionService;
import com.fthlbot.discordbotfthl.DatabaseModels.Exception.LeagueException;
import com.fthlbot.discordbotfthl.DatabaseModels.Team.Team;
import com.fthlbot.discordbotfthl.DatabaseModels.Team.TeamService;
import com.fthlbot.discordbotfthl.Util.GeneralService;
import com.fthlbot.discordbotfthl.core.Annotation.CommandType;
import com.fthlbot.discordbotfthl.core.Annotation.Invoker;
import com.fthlbot.discordbotfthl.core.Handlers.Command;
import org.javacord.api.entity.message.embed.EmbedBuilder;
import org.javacord.api.event.interaction.SlashCommandCreateEvent;
import org.javacord.api.interaction.SlashCommandInteractionOption;
import org.javacord.api.interaction.callback.InteractionOriginalResponseUpdater;
import org.springframework.stereotype.Component;

import java.awt.*;
import java.util.List;
import java.util.concurrent.CompletableFuture;

@Component
@Invoker(
        alias = "edit-transaction",
        description = "Edit transaction of a Team",
        usage = "/edit-transaction <division> <team-alias> <new-value>" ,
        type = CommandType.STAFF
)
public class EditTransaction implements Command {
    private final TeamService teamService;
    private final DivisionService divisionService;

    public EditTransaction(TeamService teamService, DivisionService divisionService) {
        this.teamService = teamService;
        this.divisionService = divisionService;
    }

    @Override
    public void execute(SlashCommandCreateEvent event) {
        List<SlashCommandInteractionOption> arguments = event.getSlashCommandInteraction().getArguments();
        CompletableFuture<InteractionOriginalResponseUpdater> respondLater = event.getSlashCommandInteraction().respondLater();

        String divAlias = arguments.get(0).getStringValue().get();
        String teamAlias = arguments.get(1).getStringValue().get();
        Long newValue = arguments.get(2).getLongValue().get();

        Division div;
        Team team;
        try {
            div = divisionService.getDivisionByAlias(divAlias);
            team = teamService.getTeamByDivisionAndAlias(teamAlias, div);

            int old = team.getAllowRosterChangesLeft();
            team = teamService.editTransaction(team, newValue.intValue());
            Team finalTeam = team;
            respondLater.thenAccept(res -> {
                EmbedBuilder eb = new EmbedBuilder()
                        .setTitle("Transaction Edited")
                        .addField("Team Name", finalTeam.getName())
                        .addField("Division", finalTeam.getDivision().getAlias())
                        .addField("Old Points", old + "")
                        .addField("New Points", finalTeam.getAllowRosterChangesLeft()+"")
                        .setAuthor(event.getSlashCommandInteraction().getUser().getDiscriminatedName())
                        .setColor(Color.CYAN)
                        .setFooter("Transaction Edited Successfully");
                res.addEmbed(eb).update();
            });

        } catch (LeagueException e) {
            GeneralService.leagueSlashErrorMessage(event, e);
        }
    }
}
