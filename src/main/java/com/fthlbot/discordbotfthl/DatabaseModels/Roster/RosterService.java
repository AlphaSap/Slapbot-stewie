package com.fthlbot.discordbotfthl.DatabaseModels.Roster;

import com.fthlbot.discordbotfthl.DatabaseModels.Exception.*;
import com.fthlbot.discordbotfthl.DatabaseModels.Team.Team;
import com.fthlbot.discordbotfthl.DatabaseModels.Team.TeamService;
import com.fthlbot.discordbotfthl.Util.BotConfig;
import org.javacord.api.entity.user.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Service
public class RosterService {

    private final RosterRepo repo;
    private final TeamService teamService;
    private final BotConfig config;
    private final Logger log = LoggerFactory.getLogger(RosterService.class);

    public RosterService(RosterRepo repo, TeamService teamService, BotConfig config) {
        this.repo = repo;
        this.teamService = teamService;
        this.config = config;
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
    //only reps
    //change has transactions
    //check pos
    public Roster addToRoster(Roster roster, User user) throws EntityAlreadyExistsException, NoMoreRosterChangesLeftException, IncorrectTownHallException, NotTheRepException {
        Optional<Roster> alreadyAddedAccount = repo.findRosterByPlayerTagAndDivision(roster.getPlayerTag(), roster.getDivision());
        List<Roster> rosterByTeam = repo.findRosterByTeam(roster.getTeam());
        if (alreadyAddedAccount.isPresent()){
            throw new EntityAlreadyExistsException(
                    "`"+alreadyAddedAccount.get().getPlayerTag()+"` is already rostered with the team `"+alreadyAddedAccount.get().getTeam().getName()+
                            "`"
            );
        }

        boolean isRep =
                roster.getTeam().getRep1ID().equals(user.getId())
                ||
                roster.getTeam().getRep2ID().equals(user.getId());
        if (!isRep){
            throw new NotTheRepException(user, roster.getTeam());
        }

        if (rosterByTeam.size() >= roster.getDivision().getRosterSize()){
            throw new NoMoreRosterChangesLeftException(roster.getTeam());
        }

        Roster finalRoster = roster;
        boolean isCorrectTh = Arrays.stream(roster.getDivision().getAllowedTownHall()).anyMatch(x -> x == finalRoster.getTownHallLevel().intValue());

        if (!isCorrectTh){
            throw new IncorrectTownHallException(finalRoster.getTownHallLevel(), finalRoster.getDivision());
        }
        roster = repo.save(roster);
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
