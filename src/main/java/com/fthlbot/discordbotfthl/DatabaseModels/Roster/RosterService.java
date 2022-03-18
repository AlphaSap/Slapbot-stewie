package com.fthlbot.discordbotfthl.DatabaseModels.Roster;

import com.fthlbot.discordbotfthl.DatabaseModels.Exception.EntityAlreadyExistsException;
import com.fthlbot.discordbotfthl.DatabaseModels.Exception.EntityNotFoundException;
import com.fthlbot.discordbotfthl.DatabaseModels.Exception.NoMoreRosterChangesLeftException;
import com.fthlbot.discordbotfthl.DatabaseModels.Team.Team;
import com.fthlbot.discordbotfthl.DatabaseModels.Team.TeamService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class RosterService {

    private final RosterRepo repo;
    private final TeamService teamService;

    @Autowired
    public RosterService(RosterRepo repo, TeamService teamService) {
        this.repo = repo;
        this.teamService = teamService;
    }

    public List<Roster> getRosterForATeam(Team team) throws EntityNotFoundException {
        List<Roster> roster = repo.findRosterByTeam(team);
        if (roster.isEmpty()){
            //TODO Convert this into a warning later, it's not an error
            throw new EntityNotFoundException("Roster for this team is empty");
        }
        return roster;
    }

    //TODO fp checks
    public Roster addToRoster(Roster roster) throws EntityAlreadyExistsException, NoMoreRosterChangesLeftException {
        Optional<Roster> alreadyAddedAccount = repo.findRosterByPlayerTagAndDivision(roster.getPlayerTag(), roster.getDivision());
        List<Roster> rosterByTeam = repo.findRosterByTeam(roster.getTeam());
        if (alreadyAddedAccount.isPresent()){
            throw new EntityAlreadyExistsException(
                    "`"+alreadyAddedAccount.get().getPlayerTag()+"` is already rostered with the team `"+alreadyAddedAccount.get().getTeam().getName()
            );
        }
        if (rosterByTeam.size() <= 0){
            String s = String.format("`%s` has no more roster changes left! No more accounts can be added!", roster.getTeam().getName());
            throw new NoMoreRosterChangesLeftException(s);
        }
        roster = repo.save(roster);
        return roster;
    }

    //TODO Replace this method with the above method! 
    public Roster addToRoster(Roster roster, Boolean decrementRosterChange) throws EntityAlreadyExistsException, EntityNotFoundException {
        Optional<Roster> alreadyAddedAccount = repo.findRosterByPlayerTagAndDivision(roster.getPlayerTag(), roster.getDivision());

        //TODO - add decrement!
        if (decrementRosterChange){
            Team team = roster.getTeam();
        }
        if (alreadyAddedAccount.isPresent()){
            throw new EntityAlreadyExistsException(
                    "`"+alreadyAddedAccount.get().getPlayerTag()+"` is already rostered with the team `"+alreadyAddedAccount.get().getTeam().getName()
            );
        }
        roster = repo.save(roster);
        teamService.decrementRC(roster.getTeam());
        return roster;
    }

    public void removeFromRoster(Team team, String tag) throws EntityNotFoundException {
        Optional<Roster> roster = repo.findRosterByTeamAndPlayerTag(team, tag);
        if (roster.isPresent()){
            repo.delete(roster.get());
            return;
        }
        throw new EntityNotFoundException(String.format("""
                `%s` is not present on your roster
                """, tag));
    }
}
