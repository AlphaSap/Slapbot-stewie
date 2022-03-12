package com.fthlbot.discordbotfthl.DatabaseModels.ScheduleWar.Schedule;

import org.springframework.stereotype.Service;

import javax.persistence.SequenceGenerator;

@Service
public class ScheduleService {
    private final ScheduleRepo repo;

    public ScheduleService(ScheduleRepo repo) {
        this.repo = repo;
    }
}
