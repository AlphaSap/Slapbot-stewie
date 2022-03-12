package com.fthlbot.discordbotfthl.DatabaseModels.ScheduleWar.Schedule;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ScheduleRepo extends JpaRepository<ScheduledWar, Integer> {
}
