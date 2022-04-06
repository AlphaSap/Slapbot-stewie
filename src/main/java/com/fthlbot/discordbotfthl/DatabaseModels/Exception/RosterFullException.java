package com.fthlbot.discordbotfthl.DatabaseModels.Exception;

import com.fthlbot.discordbotfthl.DatabaseModels.Team.Team;

public class RosterFullException extends LeagueException{
    private static final String MESSAGE = "No more accounts can be added! Roster has reached its limit";
    private final Team team;
    public RosterFullException(Team team) {
        super(MESSAGE);
        this.team = team;
    }
}
