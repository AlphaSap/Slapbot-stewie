package com.fthlbot.discordbotfthl.Commands.CommandImpl.LeagueCommandsImpl;

import Core.Enitiy.clan.ClanModel;
import Core.Enitiy.clanwar.ClanWarMember;
import Core.Enitiy.clanwar.WarInfo;
import Core.JClash;
import Core.exception.ClashAPIException;
import com.fthlbot.discordbotfthl.core.Annotation.CommandType;
import com.fthlbot.discordbotfthl.core.Annotation.Invoker;
import com.fthlbot.discordbotfthl.Commands.CommandListener.CheckLineUpListener;
import com.fthlbot.discordbotfthl.DatabaseModels.Exception.EntityNotFoundException;
import com.fthlbot.discordbotfthl.DatabaseModels.Exception.LeagueException;
import com.fthlbot.discordbotfthl.DatabaseModels.Roster.Roster;
import com.fthlbot.discordbotfthl.DatabaseModels.Roster.RosterService;
import com.fthlbot.discordbotfthl.DatabaseModels.ScheduleWar.Schedule.ScheduleWarService;
import com.fthlbot.discordbotfthl.DatabaseModels.ScheduleWar.Schedule.ScheduledWar;
import com.fthlbot.discordbotfthl.DatabaseModels.Team.Team;
import com.fthlbot.discordbotfthl.Util.Exception.ClashExceptionHandler;
import com.fthlbot.discordbotfthl.Util.GeneralService;
import org.javacord.api.entity.message.embed.EmbedBuilder;
import org.javacord.api.event.interaction.SlashCommandCreateEvent;
import org.javacord.api.interaction.callback.InteractionOriginalResponseUpdater;
import org.javacord.api.util.logging.ExceptionLogger;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.awt.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

@Component
@Invoker(
        alias = "check-lineup",
        description = "Check the line up of a war between two teams",
        usage = "/check-lineup <Schedule ID>",
        type = CommandType.ROSTER_MANAGEMENT
)
public class CheckLineUpImpl implements CheckLineUpListener {
    private final ScheduleWarService scheduledWarService;
    private final RosterService rosterService;

    //Add logger
    private final Logger log = LoggerFactory.getLogger(CheckLineUpImpl.class);

    public CheckLineUpImpl(ScheduleWarService scheduledWarService, RosterService rosterService) {
        this.scheduledWarService = scheduledWarService;
        this.rosterService = rosterService;
    }

    @Override
    public void execute(SlashCommandCreateEvent event) {
        String stringValue = event.getSlashCommandInteraction().getArguments().get(0).getStringValue().get();
        int id;
        try {
            id = Integer.parseInt(stringValue);
        } catch (NumberFormatException e) {
            event.getSlashCommandInteraction().createImmediateResponder().setContent("Invalid schedule ID").respond();
            return;
        }
        CompletableFuture<InteractionOriginalResponseUpdater> respond = event.getSlashCommandInteraction().respondLater();
        ScheduledWar scheduleById;
        try {
            scheduleById = scheduledWarService.getScheduleById(id);
        } catch (EntityNotFoundException e) {
            GeneralService.leagueSlashErrorMessage(respond, e);
            return;
        }
        JClash clash = new JClash();

        ClanModel clan1;
        ClanModel clan2;
        try {
            clan1 = clash.getClan(scheduleById.getTeamA().getTag()).join();
        } catch (IOException e) {
            e.printStackTrace();
            GeneralService.leagueSlashErrorMessage(respond, "An error occurred while fetching the line up: " + e.getMessage());
            return;
        } catch (ClashAPIException e) {
            ClashExceptionHandler handler = new ClashExceptionHandler();
            handler.setResponder(respond.join());
            handler.setStatusCode(Integer.valueOf(e.getMessage()));
            handler.createEmbed(scheduleById.getTeamA().getTag());
            handler.respond();
            return;
        }

        try {
            clan2 = clash.getClan(scheduleById.getTeamB().getTag()).join();
        } catch (IOException e) {
            e.printStackTrace();
            GeneralService.leagueSlashErrorMessage(respond, "An error occurred while fetching the line up: " + e.getMessage());
            return;
        } catch (ClashAPIException e) {
            ClashExceptionHandler handler = new ClashExceptionHandler();
            handler.setResponder(respond.join());
            handler.setStatusCode(Integer.valueOf(e.getMessage()));
            handler.createEmbed(scheduleById.getTeamB().getTag());
            handler.respond();
            return;
        }

        if (!(clan1.isWarLogPublic() && clan2.isWarLogPublic())) {
            GeneralService.leagueSlashErrorMessage(respond, "War logs are not public for one or both of the teams");
            return;
        }

        WarInfo join = null;
        try {
            join = clash.getCurrentWar(clan1.getTag()).join();

            if (join.getState().equalsIgnoreCase("notInWar")) {
                respond.thenAccept(updater -> updater.setContent("No war is currently ongoing").update());
                return;
            }

            if (!(join.getEnemy().getTag().equalsIgnoreCase(scheduleById.getTeamB().getTag()))) {
                GeneralService.leagueSlashErrorMessage(respond, "The war is not currently ongoing for the team you specified");
                return;
            }

            List<String> homeTeam = checkPlayersFromWarModel(join.getClan().getWarMembers(), scheduleById.getTeamA());
            List<String> enemyTeam = checkPlayersFromWarModel(join.getEnemy().getWarMembers(), scheduleById.getTeamB());

            if (homeTeam.isEmpty() && enemyTeam.isEmpty()) {
                respond.thenApply(updater -> updater.setContent("Lineups from both teams is OK! No Unregistered accounts found!").update());
                return;
            }
            respond.thenApply(updater -> updater.setContent("Unregistered Accounts found!!").update());

            if (!homeTeam.isEmpty()) {
                EmbedBuilder builder = new EmbedBuilder();
                builder.setTitle("Unregistered Accounts on " + scheduleById.getTeamA().getName());
                builder.setDescription("\n" + String.join("\n", "`"+homeTeam+"`"));
                builder.setTimestampToNow();
                builder.setColor(Color.RED);

                event.getSlashCommandInteraction().getChannel().get().sendMessage(builder).exceptionally(ExceptionLogger.get());
              //  event.getSlashCommandInteraction().createFollowupMessageBuilder().addEmbed(builder).send().exceptionally(ExceptionLogger.get());
            }
            if (!enemyTeam.isEmpty()) {
                EmbedBuilder builder = new EmbedBuilder();
                builder.setTitle("Unregistered Accounts on " + scheduleById.getTeamB().getName());
                builder.setDescription("\n" + String.join("\n", "`"+enemyTeam+"`"));
                builder.setTimestampToNow();
                builder.setColor(Color.RED);
                event.getSlashCommandInteraction().getChannel().get().sendMessage(builder).exceptionally(ExceptionLogger.get());

                // event.getSlashCommandInteraction().createFollowupMessageBuilder().addEmbed(builder).send().exceptionally(ExceptionLogger.get());
            }

        } catch (ClashAPIException e) {
            ClashExceptionHandler handler = new ClashExceptionHandler();
            handler.setResponder(respond.join());
            handler.setStatusCode(Integer.valueOf(e.getMessage()));
            handler.createEmbed(scheduleById.getTeamB().getTag());
            handler.respond();
            return;
        } catch (IOException e) {
            e.printStackTrace();
            GeneralService.leagueSlashErrorMessage(respond, "An error occurred while fetching the line up: " + e.getMessage());
            return;
        } catch (LeagueException e) {
            e.printStackTrace();
            GeneralService.leagueSlashErrorMessage(respond, "An error occurred while fetching the line up: " + e.getMessage());
        }

    }

    public List<String> checkPlayersFromWarModel(List<ClanWarMember> warMembers, Team team) throws EntityNotFoundException {
        List<String> unregisteredAccounts = new ArrayList<>();
        List<Roster> rosterForATeam = rosterService.getRosterForATeam(team);

        warMembers.forEach(x -> {
            boolean b = rosterForATeam.stream().anyMatch(y -> y.getPlayerTag().equalsIgnoreCase(x.getTag()));
            if (!b) {
                log.info("Player " + x.getTag() + " is not registered for team " + team.getTag());
                unregisteredAccounts.add(x.getTag());
            }
        });
        return unregisteredAccounts;
    }

}
