package com.fthlbot.discordbotfthl.Commands.CommandImpl.LeagueCommandsImpl.UtilCommands;

import com.fthlbot.discordbotfthl.Annotation.CommandType;
import com.fthlbot.discordbotfthl.Annotation.Invoker;
import com.fthlbot.discordbotfthl.Commands.CommandListener.TeamInfoListener;
import com.fthlbot.discordbotfthl.DatabaseModels.Division.Division;
import com.fthlbot.discordbotfthl.DatabaseModels.Division.DivisionService;
import com.fthlbot.discordbotfthl.DatabaseModels.Exception.LeagueException;
import com.fthlbot.discordbotfthl.DatabaseModels.Roster.Roster;
import com.fthlbot.discordbotfthl.DatabaseModels.Roster.RosterService;
import com.fthlbot.discordbotfthl.DatabaseModels.Team.Team;
import com.fthlbot.discordbotfthl.DatabaseModels.Team.TeamService;
import com.fthlbot.discordbotfthl.Util.GeneralService;
import org.javacord.api.entity.message.embed.EmbedBuilder;
import org.javacord.api.entity.user.User;
import org.javacord.api.event.interaction.SlashCommandCreateEvent;
import org.javacord.api.interaction.callback.InteractionOriginalResponseUpdater;
import org.springframework.stereotype.Component;

import java.awt.*;
import java.util.List;
import java.util.concurrent.CompletableFuture;

@Component
@Invoker(
        alias = "team-information",
        description = "Get information about a team",
        usage = "team-information <team name>",
        type = CommandType.TEAM
)
public class TeamInfoImpl implements TeamInfoListener {
    private final TeamService teamService;
    private final DivisionService divisionService;
    private final RosterService rosterService;

    public TeamInfoImpl(TeamService teamService, DivisionService divisionService, RosterService rosterService) {
        this.teamService = teamService;
        this.divisionService = divisionService;
        this.rosterService = rosterService;
    }

    @Override
    public void execute(SlashCommandCreateEvent event) {
        CompletableFuture<InteractionOriginalResponseUpdater>  responder = event.getSlashCommandInteraction().respondLater();

        String divisionAlias = event.getSlashCommandInteraction().getArguments().get(0).getStringValue().get();
        String teamIdentifier = event.getSlashCommandInteraction().getArguments().get(1).getStringValue().get();

        Division divisionByAlias;
        Team teamByDivisionAndAlias;
        List<Roster> roster;
        try {
            divisionByAlias = divisionService.getDivisionByAlias(divisionAlias);
            teamByDivisionAndAlias = teamService.getTeamByDivisionAndAlias(teamIdentifier, divisionByAlias);
            roster = rosterService.getRosterForATeam(teamByDivisionAndAlias);

        } catch (LeagueException e) {
            GeneralService.leagueSlashErrorMessage(responder,e);
            return;
        }
        User user = event.getApi().getUserById(teamByDivisionAndAlias.getRep1ID()).join();
        User user2 = event.getApi().getUserById(teamByDivisionAndAlias.getRep2ID()).join();
        //Create an embedBuilder and add the team information, don't send the embedBuilder yet
        EmbedBuilder embedBuilder = new EmbedBuilder();
        embedBuilder.setTitle("Team Information")
                .setDescription(teamByDivisionAndAlias.getName())
                .addInlineField("Division", divisionByAlias.getAlias())
                .addInlineField("Team Alias", teamByDivisionAndAlias.getAlias())
                .addInlineField("Clan tag", teamByDivisionAndAlias.getTag())
                .addInlineField("Representatives", user.getName() + " \n " + user2.getName())
                .addInlineField("Roster Size", roster.size() + "")
                .addInlineField("Transaction left", teamByDivisionAndAlias.getAllowRosterChangesLeft() + "")
                .setColor(Color.GREEN);

        //Send the embedBuilder
        responder.thenAccept(updater -> updater.addEmbed(embedBuilder).update());
    }
}

