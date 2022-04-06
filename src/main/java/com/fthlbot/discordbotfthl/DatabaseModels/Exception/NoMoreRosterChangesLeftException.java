package com.fthlbot.discordbotfthl.DatabaseModels.Exception;

import com.fthlbot.discordbotfthl.DatabaseModels.Team.Team;

public class NoMoreRosterChangesLeftException extends LeagueException {
    private static final String MESSAGE = "`%s` has no more roster changes left! No more accounts can be added!";
    private final Team team;
    public NoMoreRosterChangesLeftException(Team team) {
        super(MESSAGE.formatted(team.getName()));
        this.team = team;
    }

    public Team getTeam() {
        return team;
    }
}
