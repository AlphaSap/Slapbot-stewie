package com.fthlbot.discordbotfthl.DatabaseModels.Division;

import com.fthlbot.discordbotfthl.DatabaseModels.Exception.EntityNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class DivisionService {
    private final DivisionRepo repo;

    public DivisionService(DivisionRepo repo) {
        this.repo = repo;
    }

    public Division getDivisionByAlias(String alias) throws EntityNotFoundException {
        Optional<Division> division = repo.findDivisionByAlias(alias);
        if (division.isPresent()){
            return division.get();
        }
        throw new EntityNotFoundException("A division with the alias `"+alias+"` does not exists");
    }

    public void createDivisions(){
        List<Division> divisions = List.of(
                new Division(1, "FORGOTTEN 5", "F5", 10, 5, new Integer[]{5}),
                new Division(2, "FORGOTTEN 8", "F8", 20, 10, new Integer[]{8}),
                new Division(3, "FORGOTTEN 9", "F9", 30, 15, new Integer[]{9}),
                new Division(4, "FORGOTTEN 10", "F10", 30, 15, new Integer[]{10}),
                new Division(5, "FORGOTTEN 11", "F11", 10, 5, new Integer[]{11}),
                new Division(6, "FORGOTTEN MIX", "FMIX", 40, 20, new Integer[]{11, 10, 9, 8})
        );
      //  divisions.forEach(repo::save);
    }
}
