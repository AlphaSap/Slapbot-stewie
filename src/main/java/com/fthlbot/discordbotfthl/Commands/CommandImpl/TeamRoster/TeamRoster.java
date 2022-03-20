package com.fthlbot.discordbotfthl.Commands.CommandImpl.TeamRoster;

import com.fthlbot.discordbotfthl.Annotation.CommandType;
import com.fthlbot.discordbotfthl.Annotation.Invoker;
import com.fthlbot.discordbotfthl.DatabaseModels.Division.DivisionService;
import com.fthlbot.discordbotfthl.DatabaseModels.Roster.RosterService;
import com.fthlbot.discordbotfthl.DatabaseModels.Team.TeamService;
import com.fthlbot.discordbotfthl.Handlers.Command;
import org.javacord.api.event.interaction.SlashCommandCreateEvent;
import org.springframework.stereotype.Component;

@Invoker(
        alias = "team-roster",
        description = "fetches the roster for a team in a given division!",
        usage = "/team-roster <DIVISION> <TEAM ALIAS>",
        type = CommandType.ROSTER_MANAGEMENT
)
@Component
public class TeamRoster implements Command {

    private final TeamService teamService;
    private final DivisionService divisionService;
    private final RosterService rosterService;

    public TeamRoster(TeamService teamService, DivisionService divisionService, RosterService rosterService) {
        this.teamService = teamService;
        this.divisionService = divisionService;
        this.rosterService = rosterService;
    }

    @Override
    public void execute(SlashCommandCreateEvent event) {
        long nanoTime = System.nanoTime();
        TeamRosterService teamRosterService = new TeamRosterService();
        teamRosterService.execute(event, teamService, divisionService, rosterService, nanoTime);
    }
}
