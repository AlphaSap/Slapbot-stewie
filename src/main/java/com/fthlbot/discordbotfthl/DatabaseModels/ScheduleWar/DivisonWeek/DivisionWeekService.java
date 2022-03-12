package com.fthlbot.discordbotfthl.DatabaseModels.ScheduleWar.DivisonWeek;

import org.springframework.stereotype.Service;

@Service
public class DivisionWeekService {
    private final DivisionWeekRepo repo;

    public DivisionWeekService(DivisionWeekRepo repo) {
        this.repo = repo;
    }
}
