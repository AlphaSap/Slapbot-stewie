package com.fthlbot.discordbotfthl.DatabaseModels.Team;

import Core.Enitiy.player.Player;
import com.fthlbot.discordbotfthl.DatabaseModels.Division.Division;
import com.fthlbot.discordbotfthl.DatabaseModels.Exception.EntityAlreadyExistsException;
import com.fthlbot.discordbotfthl.DatabaseModels.Exception.EntityNotFoundException;
import com.fthlbot.discordbotfthl.DatabaseModels.Exception.NoMoreRosterChangesLeftException;
import com.fthlbot.discordbotfthl.DatabaseModels.Exception.NotTheRepException;
import org.javacord.api.entity.user.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
public class TeamService {
    private final TeamRepo repo;
    private final Logger log = LoggerFactory.getLogger(TeamService.class);

    @Autowired
    public TeamService(TeamRepo repo) {
        this.repo = repo;
    }
    public Team saveTeam(Team team) throws EntityAlreadyExistsException {
        Optional<Team> teamByTag = repo.findTeamByTag(team.getTag());
        if (teamByTag.isPresent() && Objects.equals(teamByTag.get().getDivision().getId(), team.getDivision().getId())) {
            throw new EntityAlreadyExistsException(
                    teamByTag.get().getName() + " has already registered with the same clan tag. Please use a different clan tag"
            );
        }
        Optional<Team> teamByAliasAndDivision = repo.findTeamByAliasAndDivision(team.getAlias(), team.getDivision());
        if (teamByAliasAndDivision.isPresent()){
            throw new EntityAlreadyExistsException(
                    teamByAliasAndDivision.get().getName() + " has already registered with the same alias in the same division. Please use the different alias!"
            );
        }
        team = repo.save(team);
        return team;
    }
    
    public Team getTeamByDivisionAndAlias(String finder, Division division) throws EntityNotFoundException {
        Optional<Team> teamByAliasAndDivision = repo.findTeamByAliasAndDivision(finder, division);
        Optional<Team> teamByNameAndDivision = repo.findTeamByNameAndDivision(finder, division);

        //Return the team if found
        if(teamByAliasAndDivision.isPresent()){
            return teamByAliasAndDivision.get();
        }
        if (teamByNameAndDivision.isPresent())
            return teamByNameAndDivision.get();


        //Throw exception if team is not present
        throw new EntityNotFoundException(String.format(
                """
                unable to find the team with the alias `%s` in the given division
                """
                , finder
        ));
    }

    public Team decrementRC (Team team) throws NoMoreRosterChangesLeftException {
        Integer changesLeft = team.getAllowRosterChangesLeft();
        if (changesLeft <= 0){
            throw new NoMoreRosterChangesLeftException(team);
        }
        team.setAllowRosterChangesLeft(changesLeft - 1);
        team = repo.save(team);
        return team;
    }

    public List<Team> getAllTeamsByDivision(Division division){
        return repo.findTeamByDivision(division);
    }

    public Team changeRep(User NewUser, User oldUser, Team team) throws NotTheRepException {
        log.info("old user: {} new user: {} ", oldUser.getDiscriminatedName(), NewUser.getDiscriminatedName());

        if (oldUser.getId() == team.getRep1ID()){
            team.setRep1ID(NewUser.getId());
        }else if (oldUser.getId() == team.getRep2ID()){
            team.setRep2ID(NewUser.getId());
        }else {
            throw new NotTheRepException(oldUser, team);
        }
        repo.save(team);
        return team;
    }

    public Team changeTag(Team team, String tag, String name) {
        team.setTag(tag);
        team.setName(name);
        team = repo.save(team);
        return team;
    }
    public Team changeAlias(Team team, String alias) {
        team.setAlias(alias);
        team = repo.save(team);
        return team;
    }

    public Team getTeamByID(int id) throws EntityNotFoundException {
        Optional<Team> byId = repo.findById(id);

        if (byId.isPresent()) {
            return byId.get();
        }
        throw new EntityNotFoundException("Team with the ID: " + id +" not found!");
    }

    public Team updateTeam(Team team) {
        return repo.save(team);
    }
}
