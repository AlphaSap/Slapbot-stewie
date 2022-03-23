package com.fthlbot.discordbotfthl.DatabaseModels.Exception;

import com.fthlbot.discordbotfthl.DatabaseModels.Division.Division;

public class IncorrectTownHallException extends LeagueException{

    private final Integer townHallLevel;
    private final Division division;

    private static final String MESSAGE = "Cannot add a th%d to %s";
    public IncorrectTownHallException(Integer townHallLevel, Division division) {
        super(String.format(MESSAGE, townHallLevel, division));
        this.townHallLevel = townHallLevel;
        this.division = division;
    }

    public Integer getTownHallLevel() {
        return townHallLevel;
    }

    public Division getDivision() {
        return division;
    }
}
