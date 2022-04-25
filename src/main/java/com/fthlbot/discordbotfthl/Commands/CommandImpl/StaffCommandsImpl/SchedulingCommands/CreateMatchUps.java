package com.fthlbot.discordbotfthl.Commands.CommandImpl.StaffCommandsImpl.SchedulingCommands;

import com.fthlbot.discordbotfthl.Annotation.CommandType;
import com.fthlbot.discordbotfthl.Annotation.Invoker;
import com.fthlbot.discordbotfthl.DatabaseModels.Exception.LeagueException;
import com.fthlbot.discordbotfthl.DatabaseModels.ScheduleWar.DivisonWeek.DivisionWeekService;
import com.fthlbot.discordbotfthl.DatabaseModels.ScheduleWar.DivisonWeek.DivisionWeeks;
import com.fthlbot.discordbotfthl.DatabaseModels.ScheduleWar.Schedule.ScheduleWarService;
import com.fthlbot.discordbotfthl.DatabaseModels.ScheduleWar.Schedule.ScheduledWar;
import com.fthlbot.discordbotfthl.DatabaseModels.Team.Team;
import com.fthlbot.discordbotfthl.DatabaseModels.Team.TeamService;
import com.fthlbot.discordbotfthl.Handlers.Command;
import com.fthlbot.discordbotfthl.Util.GeneralService;
import com.fthlbot.discordbotfthl.Util.Utils;
import org.javacord.api.event.interaction.SlashCommandCreateEvent;
import org.javacord.api.interaction.SlashCommandInteractionOption;
import org.javacord.api.interaction.callback.InteractionOriginalResponseUpdater;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

@Component
@Invoker(
        alias = "create-matchups",
        description = "A command to set match-ups for a specific division",
        usage = "/create-matchups <JSON>",
        type = CommandType.STAFF
)
public class CreateMatchUps implements Command {

    private final TeamService teamService;
    private final ScheduleWarService scheduleWarService;
    private final DivisionWeekService divisionWeekService;
    private final Logger log = LoggerFactory.getLogger(this.getClass());
    private final Utils utils;

    public CreateMatchUps(TeamService teamService, ScheduleWarService scheduleWarService, DivisionWeekService divisionWeekService, Utils utils) {
        this.teamService = teamService;
        this.scheduleWarService = scheduleWarService;
        this.divisionWeekService = divisionWeekService;
        this.utils = utils;
    }
/** todo
# Sample JSON
{
  "divWeekID": 1,
  "schedule": [
    {
      "home": 9,
      "enemy": 9
    },
    {
      "home": 9,
      "enemy": 9
    },
    {
      "home": 9,
      "enemy": 9
    }
  ]
}*/
    //TODO a command to get division Week ID
    public void create(JSONObject jsonObject) throws LeagueException {
        int divWeekID = jsonObject.getInt("divWeekID");
        var jsonArray = jsonObject.getJSONArray("schedule");

        int length = jsonArray.length();
        List<ScheduledWar> scheduledWarList = new ArrayList<>();
        for (int i = 0; i < length; i++) {
            int home = jsonArray.getJSONObject(i).getInt("home");
            int enemy = jsonArray.getJSONObject(i).getInt("enemy");

            Team homeTeam = teamService.getTeamByID(home);
            Team enemyTeam = teamService.getTeamByID(enemy);

            DivisionWeeks divWeekByID = divisionWeekService.getDivWeekByID(divWeekID);
            ScheduledWar war = new ScheduledWar(
                    divWeekByID,
                    homeTeam,
                    enemyTeam
            );
            if (!homeTeam.getDivision().getId().equals(enemyTeam.getDivision().getId())){
                String s = """
                        Both teams must be in the same division.
                        %s and %s are not in the same division.
                        `%d` , `%d`
                        Index: %d
                        """.formatted(homeTeam.getName(), enemyTeam.getName(), homeTeam.getID(), enemyTeam.getID(), (i+1));
                throw new LeagueException(s);
            }
            scheduledWarList.add(war);
        }
        scheduleWarService.saveSchedule(scheduledWarList);
        log.info("Added schedules successfully");
    }

    @Override
    public void execute(SlashCommandCreateEvent event) {
        CompletableFuture<InteractionOriginalResponseUpdater> respondLater = event.getSlashCommandInteraction().respondLater();
        List<SlashCommandInteractionOption> arguments = event.getSlashCommandInteraction().getArguments();
        String json = arguments.get(0).getStringValue().get();
        JSONObject jsonObject = utils.getJsonObject(event, respondLater, json);

        if (jsonObject == null) return;

        try {
            create(jsonObject);
            respondLater.thenAccept(res -> {
                res.setContent("Teams extracted from json").update();
            });
        } catch (LeagueException e) {
            GeneralService.leagueSlashErrorMessage(respondLater, e);
        }catch (Exception e){   //TODO
            GeneralService.sendFatalError(respondLater, e);
        }
    }

}
