package com.fthlbot.discordbotfthl.DatabaseModels.Team;

import com.fthlbot.discordbotfthl.DatabaseModels.Division.Division;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TeamRepo extends JpaRepository<Team, Integer> {

    Optional<Team> findTeamByTagAndDivision(String tag, Division division);
    Optional<Team> findTeamByAliasAndDivision(String alias, Division division);

    Optional<Team> findTeamByDivisionAndAlias(Division division, String alias);

    Optional<Team> findTeamByNameAndDivision(String name, Division division );

    List<Team> findTeamByDivision(Division division);

    List<Team> findTeamByRep1IDOrRep2ID(long id, long id2);
}
