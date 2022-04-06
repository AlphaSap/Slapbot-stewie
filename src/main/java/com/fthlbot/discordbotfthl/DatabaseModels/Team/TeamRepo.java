package com.fthlbot.discordbotfthl.DatabaseModels.Team;

import com.fthlbot.discordbotfthl.DatabaseModels.Division.Division;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TeamRepo extends JpaRepository<Team, Integer> {

    Optional<Team> findTeamByTag(String tag);
    Optional<Team> findTeamByAliasAndDivision(String alias, Division division);

    Optional<Team> findTeamByDivisionAndAlias(Division division, String alias);

    Optional<Team> findTeamByNameAndDivision(String name, Division division );

    List<Team> findTeamByDivision(Division division);

}
