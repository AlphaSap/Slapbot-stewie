package com.fthlbot.discordbotfthl.Commands.CommandImpl.StaffCommandsImpl.SchedulingCommands;

import com.fthlbot.discordbotfthl.Util.Pagination.ButtonRemoveJobScheduler;
import com.fthlbot.discordbotfthl.Util.Pagination.ButtonRemoveJobs;
import com.fthlbot.discordbotfthl.core.Annotation.CommandType;
import com.fthlbot.discordbotfthl.core.Annotation.Invoker;
import com.fthlbot.discordbotfthl.DatabaseModels.Exception.LeagueException;
import com.fthlbot.discordbotfthl.DatabaseModels.ScheduleWar.DivisonWeek.DivisionWeekService;
import com.fthlbot.discordbotfthl.DatabaseModels.ScheduleWar.DivisonWeek.DivisionWeeks;
import com.fthlbot.discordbotfthl.DatabaseModels.ScheduleWar.Schedule.ScheduleWarService;
import com.fthlbot.discordbotfthl.DatabaseModels.ScheduleWar.Schedule.ScheduledWar;
import com.fthlbot.discordbotfthl.DatabaseModels.Team.Team;
import com.fthlbot.discordbotfthl.DatabaseModels.Team.TeamService;
import com.fthlbot.discordbotfthl.core.Handlers.Command;
import com.fthlbot.discordbotfthl.Util.GeneralService;
import com.fthlbot.discordbotfthl.Util.Utils;
import org.javacord.api.entity.message.Message;
import org.javacord.api.entity.message.component.ActionRow;
import org.javacord.api.entity.message.component.Button;
import org.javacord.api.entity.message.component.ButtonStyle;
import org.javacord.api.entity.message.embed.EmbedBuilder;
import org.javacord.api.event.interaction.SlashCommandCreateEvent;
import org.javacord.api.interaction.SlashCommandInteractionOption;
import org.javacord.api.interaction.callback.InteractionOriginalResponseUpdater;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

@Component
@Invoker(
        alias = "create-matchups",
        description = "A command to set match-ups for a specific division",
        usage = "/create-matchups <JSON>",
        type = CommandType.DEV
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

    /**
     * todo
     * # Sample JSON
     * {
     * "divWeekID": 1,
     * "schedule": [
     * {
     * "home": 9,
     * "enemy": 9
     * },
     * {
     * "home": 9,
     * "enemy": 9
     * },
     * {
     * "home": 9,
     * "enemy": 9
     * }
     * ]
     * }
     *
     * @return
     */
    //TODO a command to get division Week ID
    public List<ScheduledWar> create(JSONObject jsonObject) throws LeagueException {
        int divWeekID = jsonObject.getInt("divWeekID");
        var jsonArray = jsonObject.getJSONArray("schedule");

        int length = jsonArray.length();
        DivisionWeeks divWeekByID = divisionWeekService.getDivWeekByID(divWeekID);
        if (divWeekByID.isByeWeek()) {
            throw new LeagueException("This is a bye week, no match-ups can be set");
        }
        List<ScheduledWar> scheduledWarList = new ArrayList<>();
        for (int i = 0; i < length; i++) {
            int home = jsonArray.getJSONObject(i).getInt("home");
            int enemy = jsonArray.getJSONObject(i).getInt("enemy");

            Team homeTeam = teamService.getTeamByID(home);
            Team enemyTeam = teamService.getTeamByID(enemy);

            ScheduledWar war = new ScheduledWar(
                    divWeekByID,
                    homeTeam,
                    enemyTeam
            );
            if (!homeTeam.getDivision().getId().equals(enemyTeam.getDivision().getId())) {
                String s = """
                        Both teams must be in the same division.
                        %s and %s are not in the same division.
                        `%d` , `%d`
                        Index: %d
                        """.formatted(homeTeam.getName(), enemyTeam.getName(), homeTeam.getID(), enemyTeam.getID(), (i + 1));
                throw new LeagueException(s);
            }
            scheduledWarList.add(war);
        }
        return scheduledWarList;
    }

    @Override
    public void execute(SlashCommandCreateEvent event) {
        CompletableFuture<InteractionOriginalResponseUpdater> respondLater = event.getSlashCommandInteraction().respondLater();
        List<SlashCommandInteractionOption> arguments = event.getSlashCommandInteraction().getArguments();
        String json = arguments.get(0).getStringValue().get();
        JSONObject jsonObject = utils.getJsonObject(event, respondLater, json);

        if (jsonObject == null) return;

        try {
            List<ScheduledWar> scheduledWars = create(jsonObject);
            respondLater.thenAccept(res -> {
                res.setContent("Teams extracted from json").update();
            }).join();

            for (ScheduledWar war : scheduledWars) {
                event.getSlashCommandInteraction().createFollowupMessageBuilder()
                        .addEmbed(
                                new EmbedBuilder()
                                        .setTitle("Scheduled War")
                                        .setDescription(
                                                """
                                                        %s vs %s
                                                        """.formatted(war.getTeamA().getName(), war.getTeamB().getName())
                                        )
                                        .addField("Division", war.getDivisionWeeks().getDivision().getName(), true)
                                        .setColor(Color.orange)
                                        .setTimestampToNow()
                        ).send();
            }

            CompletableFuture<Message> send = event.getSlashCommandInteraction().createFollowupMessageBuilder()
                    .setContent("Match-ups created, Would you like to save them or try again?")
                    .addComponents(ActionRow.of(
                            List.of(
                                    Button.create("save", ButtonStyle.PRIMARY, "Save"),
                                    Button.create("exit", ButtonStyle.DANGER, "Try Again")
                            )
                    )).send();

            new ButtonRemoveJobScheduler().execute(send.join());

            send.join().addButtonClickListener(b -> {
               if (b.getButtonInteraction().getCustomId().equals("Save")) {
                   b.getButtonInteraction().acknowledge();
                   save(scheduledWars);
                   event.getSlashCommandInteraction().createFollowupMessageBuilder()
                           .setContent("Match-ups saved")
                           .send();
               } else {
                   b.getButtonInteraction().acknowledge();
                   event.getSlashCommandInteraction().createFollowupMessageBuilder()
                           .setContent("Match-ups not saved")
                           .send();
               }
            }).removeAfter(9, TimeUnit.MINUTES);

        } catch (LeagueException e) {
            GeneralService.leagueSlashErrorMessage(respondLater, e);
        } catch (Exception e) {   //TODO
            GeneralService.sendFatalError(respondLater, e);
        }
    }

    public List<ScheduledWar> save(List<ScheduledWar> wars) {
        return scheduleWarService.saveSchedule(wars);
    }
}
