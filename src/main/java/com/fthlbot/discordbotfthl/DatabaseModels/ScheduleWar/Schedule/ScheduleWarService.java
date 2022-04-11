package com.fthlbot.discordbotfthl.DatabaseModels.ScheduleWar.Schedule;

import org.springframework.stereotype.Service;

import java.util.Collection;

@Service
public class ScheduleWarService {
    private final ScheduleRepo repo;

    public ScheduleWarService(ScheduleRepo repo) {
        this.repo = repo;
    }
    public void saveSchedule(ScheduledWar scheduledWar){
        repo.save(scheduledWar);
    }
    public void saveSchedule(Collection<ScheduledWar> scheduledWar){
        repo.saveAll(scheduledWar);
    }
}
