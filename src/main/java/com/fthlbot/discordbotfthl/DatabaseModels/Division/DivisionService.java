package com.fthlbot.discordbotfthl.DatabaseModels.Division;

import com.fthlbot.discordbotfthl.DatabaseModels.Exception.EntityNotFoundException;
import org.springframework.stereotype.Service;

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
}
