package com.fthlbot.discordbotfthl.DatabaseModels.ScheduleWar.DivisonWeek;

import org.springframework.stereotype.Service;

import java.util.Collection;

@Service
public class DivisionWeekService {
    private final DivisionWeekRepo repo;

    public DivisionWeekService(DivisionWeekRepo repo) {
        this.repo = repo;
    }

    public DivisionWeeks save(DivisionWeeks divisionWeeks){
        divisionWeeks = repo.save(divisionWeeks);
        return divisionWeeks;
    }
    public Collection<DivisionWeeks> save(Collection<DivisionWeeks> divisionWeeks){
        divisionWeeks = repo.saveAll(divisionWeeks);
        return divisionWeeks;
    }
}
