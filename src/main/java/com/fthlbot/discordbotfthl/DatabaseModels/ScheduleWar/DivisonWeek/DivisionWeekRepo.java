package com.fthlbot.discordbotfthl.DatabaseModels.ScheduleWar.DivisonWeek;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DivisionWeekRepo extends JpaRepository<DivisionWeeks, Integer> {
}
