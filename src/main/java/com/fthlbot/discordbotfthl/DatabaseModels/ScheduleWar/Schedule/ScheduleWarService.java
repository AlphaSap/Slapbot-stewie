package com.fthlbot.discordbotfthl.DatabaseModels.ScheduleWar.Schedule;

import com.fthlbot.discordbotfthl.DatabaseModels.Exception.EntityNotFoundException;
import com.fthlbot.discordbotfthl.DatabaseModels.ScheduleWar.DivisonWeek.DivisionWeeks;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;

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

    //get schedule by divisionWeek
    public List<ScheduledWar> getScheduleByDivisionWeek(DivisionWeeks divisionWeek) throws EntityNotFoundException {
        //throw an error if findByDivisionWeek is null or has 0 elements
        List<ScheduledWar> byDivisionWeeks = repo.findByDivisionWeeks(divisionWeek);
        if (byDivisionWeeks.size() == 0){
            throw new EntityNotFoundException("No schedule found for this division week");
        }
        return byDivisionWeeks;
    }
}
