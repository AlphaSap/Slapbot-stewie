package com.fthlbot.discordbotfthl.DatabaseModels.Division;

import com.fthlbot.discordbotfthl.DatabaseModels.Exception.EntityNotFoundException;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class DivisionService {

    private final List<Division> divisions = List.of(
            new Division("FORGOTTEN 8", "F8", 20, 10, new Integer[] { 8 }),
            new Division("FORGOTTEN 9", "F9", 30, 10, new Integer[] { 9 }),
            new Division("FORGOTTEN 10", "F10", 30, 10, new Integer[] { 10 }),
            new Division("FORGOTTEN 11", "F11", 20, 10, new Integer[] { 11 }),
            new Division("FORGOTTEN 12", "F12", 25, 10, new Integer[] { 12 }),
            new Division("Lite", "Lite", 30, 10, new Integer[] { 5, 6, 7, 8, 9 }),
            new Division("Elite", "Elite", 40, 15, new Integer[] { 12, 11, 10, 9 }));
    private final DivisionRepo repo;

    public DivisionService(DivisionRepo repo) {
        this.repo = repo;
    }

    public Division getDivisionByAlias(String alias) throws EntityNotFoundException {
        Optional<Division> division = repo.findDivisionByAlias(alias);
        if (division.isPresent()) {
            return division.get();
        }
        throw new EntityNotFoundException("A division with the alias `" + alias + "` does not exists");
    }

    public void createDivisions() {
        repo.deleteAll();
        repo.saveAll(divisions);
    }

    public void correctTheDivision() {
        List<Division> all = repo.findAll();

        for (Division division : all) {
            for (Division correct : divisions) {
                if (correct.getAlias().equals(division.getAlias())) {
                    division.setName(correct.getName());
                    division.setAlias(correct.getAlias());
                    division.setRosterSize(correct.getRosterSize());
                    division.setAllowedRosterChanges(correct.getAllowedRosterChanges());
                    division.setAllowedTownHall(correct.getAllowedTownHall());
                    repo.save(division);
                }
            }
        }
    }
}
