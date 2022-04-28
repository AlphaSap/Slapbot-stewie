package com.fthlbot.discordbotfthl.DatabaseModels.Roster;

import com.fthlbot.discordbotfthl.DatabaseModels.Exception.*;
import com.fthlbot.discordbotfthl.DatabaseModels.Team.Team;
import com.fthlbot.discordbotfthl.DatabaseModels.Team.TeamService;
import com.fthlbot.discordbotfthl.Util.BotConfig;
import org.javacord.api.entity.user.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.springframework.data.domain.Sort.*;

@Service
public class RosterService {

    private final RosterRepo repo;
    private final TeamService teamService;
    private final BotConfig botConfig;
    private final Logger log = LoggerFactory.getLogger(RosterService.class);

    public RosterService(RosterRepo repo, TeamService teamService, BotConfig config, BotConfig botConfig) {
        this.repo = repo;
        this.teamService = teamService;
        this.botConfig = botConfig;
    }

    public List<Roster> getRosterForATeam(Team team) throws EntityNotFoundException {
        return repo.findRosterByTeam(team);
    }

    public Page<Roster> getRoster(Team team){
        return repo.findRosterByTeam(team, PageRequest.of(0, 10, by(Direction.ASC, "town_hall_level")));
    }

    //TODO fp checks
    //check pos
    public Roster addToRoster(Roster roster, User user) throws LeagueException {
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

        boolean isCorrectTh = Arrays.stream(roster.getDivision().getAllowedTownHall()).anyMatch(x -> x == roster.getTownHallLevel().intValue());

        if (!isCorrectTh){
            throw new IncorrectTownHallException(roster.getTownHallLevel(), roster.getDivision());
        }

        try {
            decrementAllowedRosterChanges(roster.getTeam());
        } catch (ParseException e) {
            throw new UnExpectedLeagueException("Failed to parse date, this should never happen\n Please report this to the developer");
        }
        return repo.save(roster);
    }

    private void decrementAllowedRosterChanges(Team team) throws NoMoreRosterChangesLeftException, ParseException {
        //decrement allowed roster changes
        //check if today is after the leagueStartDate in botConfig
        if (botConfig.getLeagueStartDate().after(new Date())){
            return;
        }
        int allowRosterChangesLeft = team.getAllowRosterChangesLeft() - 1;
        //throw exception if no more roster changes left
        if (allowRosterChangesLeft <= 0){
            throw new NoMoreRosterChangesLeftException(team);
        }
        team.setAllowRosterChangesLeft(allowRosterChangesLeft);
        teamService.updateTeam(team);
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

    //Find teams for a player tag
    public List<Team> getTeamsForPlayerTag(String tag) {
        return repo.findRosterByPlayerTag(tag)
                .stream()
                .map(Roster::getTeam)
                .collect(Collectors.toList());
    }
}
