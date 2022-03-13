package com.fthlbot.discordbotfthl.DatabaseModels.Roster;

import com.fthlbot.discordbotfthl.DatabaseModels.Division.Division;
import com.fthlbot.discordbotfthl.DatabaseModels.Team.Team;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RosterRepo extends JpaRepository<Roster, Integer> {

    List<Roster> findRosterByTeam(Team team);

    Optional<Roster> findRosterByPlayerTagAndDivision(String tag, Division division);

    Optional<Roster> findRosterByTeamAndPlayerTag(Team team, String playerTag);
}
