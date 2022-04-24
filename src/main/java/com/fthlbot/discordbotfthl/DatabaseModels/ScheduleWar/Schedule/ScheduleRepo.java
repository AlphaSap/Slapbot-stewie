package com.fthlbot.discordbotfthl.DatabaseModels.ScheduleWar.Schedule;

import com.fthlbot.discordbotfthl.DatabaseModels.ScheduleWar.DivisonWeek.DivisionWeeks;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ScheduleRepo extends JpaRepository<ScheduledWar, Integer> {
    List<ScheduledWar> findByDivisionWeeks(DivisionWeeks divisionWeeks);
}
