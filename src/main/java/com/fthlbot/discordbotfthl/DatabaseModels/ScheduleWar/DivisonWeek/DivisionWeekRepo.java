package com.fthlbot.discordbotfthl.DatabaseModels.ScheduleWar.DivisonWeek;

import com.fthlbot.discordbotfthl.DatabaseModels.Division.Division;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DivisionWeekRepo extends JpaRepository<DivisionWeeks, Integer> {

    List<DivisionWeeks> findDivisionWeeksByDivision(Division division);
}
