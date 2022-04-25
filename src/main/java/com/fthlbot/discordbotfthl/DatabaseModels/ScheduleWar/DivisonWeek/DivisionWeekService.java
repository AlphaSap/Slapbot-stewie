package com.fthlbot.discordbotfthl.DatabaseModels.ScheduleWar.DivisonWeek;

import com.fthlbot.discordbotfthl.DatabaseModels.Division.Division;
import com.fthlbot.discordbotfthl.DatabaseModels.Exception.EntityNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

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

    public DivisionWeeks getDivWeekByID(int id) throws EntityNotFoundException {
        Optional<DivisionWeeks> byId = repo.findById(id);
        if (byId.isPresent())
            return byId.get();
        throw new EntityNotFoundException("Cannot find a Division week with the ID: " + id+"");
    }
    //gets the division weeks by division
    public List<DivisionWeeks> getDivisionWeeksByDivision(Division division) throws EntityNotFoundException {
        List<DivisionWeeks> divisionWeeksByDivision = repo.findDivisionWeeksByDivision(division);
        if (divisionWeeksByDivision.size() <= 0){
            throw new EntityNotFoundException("No Weeks found for this division!");
        }
        return divisionWeeksByDivision;
    }
}
