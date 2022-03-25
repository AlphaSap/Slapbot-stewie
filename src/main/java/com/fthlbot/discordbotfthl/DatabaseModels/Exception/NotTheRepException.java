package com.fthlbot.discordbotfthl.DatabaseModels.Exception;

import com.fthlbot.discordbotfthl.DatabaseModels.Team.Team;
import org.javacord.api.entity.user.User;

public class NotTheRepException extends LeagueException{
    private static final String MESSAGE = "`%s` is not the rep for `%s`";
    private User user;
    private Team team;
    public NotTheRepException(User user, Team team) {
        super(String.format(MESSAGE, user.getDiscriminatedName(), team.getName()));
        this.user = user;
        this.team = team;
    }

    public User getUser() {
        return user;
    }

    public Team getTeam() {
        return team;
    }
}
