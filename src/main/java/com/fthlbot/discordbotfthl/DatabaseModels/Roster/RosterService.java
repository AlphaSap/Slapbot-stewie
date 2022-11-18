package com.fthlbot.discordbotfthl.DatabaseModels.Roster;

import com.fthlbot.discordbotfthl.DatabaseModels.Division.Division;
import com.fthlbot.discordbotfthl.DatabaseModels.Exception.*;
import com.fthlbot.discordbotfthl.DatabaseModels.Team.Team;
import com.fthlbot.discordbotfthl.DatabaseModels.Team.TeamService;
import com.fthlbot.discordbotfthl.Util.BotConfig;
import org.javacord.api.entity.user.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.ParseException;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

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
    //TODO fp checks
    //check pos
    public synchronized Roster addToRoster(Roster roster, User user) throws LeagueException, ParseException {
        Optional<Roster> alreadyAddedAccount = repo.findRosterByPlayerTagAndDivision(roster.getPlayerTag(), roster.getDivision());
        List<Roster> rosterByTeam = repo.findRosterByTeam(roster.getTeam());

        // check if roster change is open, does not include the grey period
        if (!isRosterChangeOpen(roster.getDivision())) {
            throw new UnExpectedLeagueException("Roster Changes for " + roster.getDivision().getAlias() + " is locked!");
        }

        // throw an exception if we are in grey period.
        if (isGreyPeriod(roster.getDivision())) {
            throw new UnExpectedLeagueException("Roster Changes for " + roster.getDivision().getAlias() + " is locked!");
        }

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
            throw new NoMoreRosterChangesLeftException(roster.getTeam(),
                    """
                    Your Roster is full!
                    You can't add anymore accounts to your team. Accounts need to be removed before you can add more.
                    """);
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
        if (!canDecrement(team.getDivision())){
            return;
        }
        int allowRosterChangesLeft = team.getAllowRosterChangesLeft();
        //throw exception if no more roster changes left
        if (allowRosterChangesLeft <= 0){
            throw new NoMoreRosterChangesLeftException(team,
                    "You have no more roster changes left! No more accounts can be added! [TRANSACTION POINTS - 0]");
        }
        team.setAllowRosterChangesLeft(allowRosterChangesLeft - 1);
        teamService.updateTeam(team);
    }
    public boolean isRosterChangeOpen(Division division) throws ParseException {
        switch (division.getAlias().toLowerCase()) {
            case "f5" -> {
                return true;
            }
            case "f8" -> {
                Date endDate = botConfig.getF8EndDate();
                return isTodayBetweenTwoDates(botConfig.getLeagueRegistrationEndDate(), endDate);
            }
            case "f9" -> {
                Date endDate = botConfig.getF9EndDate();
                return isTodayBetweenTwoDates(botConfig.getLeagueRegistrationEndDate(), endDate);
            }
            case "f10" -> {
                Date endDate = botConfig.getF10EndDate();
                return isTodayBetweenTwoDates(botConfig.getLeagueRegistrationEndDate(), endDate);
            }
            case "f11" -> {
                Date endDate = botConfig.getF11EndDate();
                return isTodayBetweenTwoDates(botConfig.getLeagueRegistrationEndDate(), endDate);
            }
            case "elite" -> {
                Date endDate = botConfig.getEliteEndDate();
                return isTodayBetweenTwoDates(botConfig.getLeagueRegistrationEndDate(), endDate);
            }
            case "lite" -> {
                Date endDate = botConfig.getLiteEndDate();
                return isTodayBetweenTwoDates(botConfig.getLeagueRegistrationEndDate(), endDate);
            }
            default -> throw new IllegalStateException("Unexpected value: " + division.getAlias().toLowerCase());
        }
    }

    /**
     * No roster addition are allowed during this period.
     * @param division
     * @return true if today's date is between
     * @throws ParseException
     */
    public boolean isGreyPeriod(Division division) throws ParseException {
        switch (division.getAlias().toLowerCase()) {
            case "f5" -> {
                return true;
            }
            case "f8", "f10", "f9", "f11", "elite", "lite" -> {
                Date startDate = botConfig.getLeagueGrepPeriodStartDate();
                Date endDate = botConfig.getLeagueGrepPeriodEndDate();
                return isTodayBetweenTwoDates(startDate, endDate);
            }
            default -> throw new IllegalStateException("Unexpected value: " + division.getAlias().toLowerCase());
        }
    }

    public boolean canDecrement(Division division) throws ParseException {
        switch (division.getAlias().toLowerCase()) {
            case "f5" -> {
                return true;
            }
            case "f8" -> {
                Date startDate = botConfig.getF8StartDate();
                Date endDate = botConfig.getF8EndDate();
                return isTodayBetweenTwoDates(startDate, endDate);
            }
            case "f9" -> {
                Date startDate = botConfig.getF9StartDate();
                Date endDate = botConfig.getF9EndDate();
                return isTodayBetweenTwoDates(startDate, endDate);
            }
            case "f10" -> {
                Date startDate = botConfig.getF10StartDate();
                Date endDate = botConfig.getF10EndDate();
                return isTodayBetweenTwoDates(startDate, endDate);
            }
            case "f11" -> {
                Date startDate = botConfig.getF11StartDate();
                Date endDate = botConfig.getF11EndDate();
                return isTodayBetweenTwoDates(startDate, endDate);
            }
            case "elite" -> {
                Date startDate = botConfig.getEliteStartDate();
                Date endDate = botConfig.getEliteEndDate();
                return isTodayBetweenTwoDates(startDate, endDate);
            }
            case "lite" -> {
                Date startDate = botConfig.getLiteStartDate();
                Date endDate = botConfig.getLiteEndDate();
                return isTodayBetweenTwoDates(startDate, endDate);
            }
            default -> throw new IllegalStateException("Unexpected value: " + division.getAlias().toLowerCase());
        }
    }

    private static boolean isTodayBetweenTwoDates(Date startDate, Date endDate) {
        if (startDate.after(new Date()))
            return false;
        return !endDate.before(new Date());
    }

    public Roster removeFromRoster(Team team, String tag, User user) throws LeagueException {
        boolean isRep =
                team.getRep1ID().equals(user.getId())
                        ||
                team.getRep2ID().equals(user.getId());

        if (!isRep){
            throw new NotTheRepException(user, team);
        }

        Optional<Roster> roster = repo.findRosterByTeamAndPlayerTag(team, tag);
        if (roster.isPresent()){
            repo.delete(roster.get());
            return roster.get();
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

    /**
     * used for removing fp check command
     * @see com.fthlbot.discordbotfthl.Commands.CommandImpl.StaffCommandsImpl.FairPlayCheckOnAllTeamImpl
     * @param roster
     */
    public void removeRoster(Roster roster) {
        repo.delete(roster);
    }

    //Make a method that remove all roster from a team
    /**
     * Used only in conjunction with the remove team method
     * @see com.fthlbot.discordbotfthl.Commands.CommandImpl.StaffCommandsImpl.DeleteATeamImpl
     * @param team
     */
    @Transactional
    public void removeAllRoster(Team team) {
        repo.deleteRosterByTeam(team);
    }

    /**
     *
     */
    @Transactional
    public synchronized Roster forceAdd (Roster player) {
        return repo.save(player);
    }
}
