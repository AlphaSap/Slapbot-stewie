package com.fthlbot.discordbotfthl.DatabaseModels.Division;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface DivisionRepo extends JpaRepository<Division, Integer> {

    Optional<Division> findDivisionByAlias(String alias);
}
