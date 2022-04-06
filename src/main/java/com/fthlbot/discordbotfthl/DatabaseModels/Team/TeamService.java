package com.fthlbot.discordbotfthl.DatabaseModels.Team;

import com.fthlbot.discordbotfthl.DatabaseModels.Division.Division;
import com.fthlbot.discordbotfthl.DatabaseModels.Exception.EntityAlreadyExistsException;
import com.fthlbot.discordbotfthl.DatabaseModels.Exception.EntityNotFoundException;
import com.fthlbot.discordbotfthl.DatabaseModels.Exception.NotTheRepException;
import org.javacord.api.entity.user.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class TeamService {
    private final TeamRepo repo;

    @Autowired
    public TeamService(TeamRepo repo) {
        this.repo = repo;
    }
    public Team saveTeam(Team team) throws EntityAlreadyExistsException {
        Optional<Team> teamByTag = repo.findTeamByTag(team.getTag());
        if (teamByTag.isPresent()){
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

    public Team decrementRC (Team team) throws EntityNotFoundException {
        Optional<Team> teamOpt = repo.findById(team.getID());

        if (teamOpt.isEmpty()){
            throw new EntityNotFoundException("Team with the ID " + team.getID() + " is not present!, please notify the admins for this error immediately ");
        }
        team = repo.updateRosterChange(team.getID());
        return team;
    }

    public List<Team> getAllTeamsByDivision(Division division){
        return repo.findTeamByDivision(division);
    }

    public Team changeRep(User NewUser, User oldUser, Team team) throws NotTheRepException {
        if (oldUser.getId() == team.getRep1ID()){
            team = repo.updateRep1(team.getID(), NewUser.getId(), oldUser.getId());
        }else if (oldUser.getId() == team.getRep2ID()){
            team = repo.updateRep2(team.getID(), NewUser.getId(), oldUser.getId());
        }else {
            throw new NotTheRepException(oldUser, team);
        }
        return team;
    }
}
