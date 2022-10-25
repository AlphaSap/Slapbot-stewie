package com.fthlbot.discordbotfthl.DatabaseModels.Team;

import com.fthlbot.discordbotfthl.DatabaseModels.BannedReps.BannedRep;
import com.fthlbot.discordbotfthl.DatabaseModels.BannedReps.BannedRepService;
import com.fthlbot.discordbotfthl.DatabaseModels.Division.Division;
import com.fthlbot.discordbotfthl.DatabaseModels.Exception.*;
import org.javacord.api.entity.user.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class TeamService {
    private final TeamRepo repo;
    private final BannedRepService bannedRep;
    private final Logger log = LoggerFactory.getLogger(TeamService.class);

   // private final RosterService rosterService;
    @Autowired
    public TeamService(TeamRepo repo, BannedRepService bannedRep) {
        this.repo = repo;
        this.bannedRep = bannedRep;
    }
    public Team saveTeam(Team team) throws EntityAlreadyExistsException, UserBannedFromReppingTeam {
        checksRep(team.getRep1ID());
        checksRep(team.getRep2ID());

        Optional<Team> teamByTag = repo.findTeamByTagAndDivision(team.getTag(), team.getDivision());

        if (teamByTag.isPresent()) {
            throw new EntityAlreadyExistsException(
                    teamByTag.get().getName() + " has already registered with the same clan tag in %s. Please use a different clan tag".formatted(team.getDivision().getAlias())

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

    @Deprecated
    private Team decrementRC (Team team) throws NoMoreRosterChangesLeftException {
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

    public Team changeRep(User NewUser, User oldUser, Team team) throws NotTheRepException, UserBannedFromReppingTeam {

        checksRep(NewUser.getId());
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

    private void checksRep(Long NewUser) throws UserBannedFromReppingTeam {
        Optional<BannedRep> repOptional = bannedRep.getBannedRep(NewUser);
        if (repOptional.isPresent()) {
            throw new UserBannedFromReppingTeam(repOptional.get()); //throw an exception if the rep is banned...
        }
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

    //Method to delete a team
    public void deleteTeam(Team team) {
       // rosterService.removeAllRoster(team);
        repo.delete(team);
    }
    /**
     * @param userID - the user id
     * @return Will return a list of team for the rep, if the rep is not found, will return an empty list
     */
    public List<Team> getTeamByRep(long userID) {
        return repo.findTeamByRep1IDOrRep2ID(userID, userID);
    }

    public List<Team> getAllTeams() {
        return repo.findAll();
    }

    public Pair<String, String> findRepAndDeleteHimFromAllTeam(long userID) {
        List<Team> teams = getTeamByRep(userID);
        StringBuilder teamName = new StringBuilder("");
        StringBuilder divName = new StringBuilder("");
        for (Team team : teams) {
            if (team.getRep1ID() == userID) {
                team.setRep1ID(team.getRep2ID());
            } else if (team.getRep2ID() == userID) {
                team.setRep2ID(team.getRep1ID());
            }

            teamName.append(team.getName()).append(", ");
            divName.append(team.getDivision().getAlias()).append(", ");
            repo.save(team);
        }
        return Pair.of(teamName.toString(), divName.toString());
    }

    public Team editTransaction(Team team, int longToInt) {
        team.setAllowRosterChangesLeft(longToInt);
        team = repo.save(team);
        return team;
    }

    public Team registrationChennelID(Team team, long channelID) {
        team = team.setRegistrationChannelID(channelID);
        team = repo.save(team);
        return team;
    }
}
