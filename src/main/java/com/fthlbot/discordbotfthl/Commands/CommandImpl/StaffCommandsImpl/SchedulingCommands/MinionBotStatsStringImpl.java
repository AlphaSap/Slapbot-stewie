package com.fthlbot.discordbotfthl.Commands.CommandImpl.StaffCommandsImpl.SchedulingCommands;

import com.fthlbot.discordbotfthl.DatabaseModels.Division.DivisionService;
import com.fthlbot.discordbotfthl.DatabaseModels.Exception.EntityNotFoundException;
import com.fthlbot.discordbotfthl.DatabaseModels.ScheduleWar.DivisonWeek.DivisionWeekService;
import com.fthlbot.discordbotfthl.DatabaseModels.ScheduleWar.DivisonWeek.DivisionWeeks;
import com.fthlbot.discordbotfthl.DatabaseModels.ScheduleWar.Schedule.ScheduleWarService;
import com.fthlbot.discordbotfthl.DatabaseModels.ScheduleWar.Schedule.ScheduledWar;
import com.fthlbot.discordbotfthl.Util.GeneralService;
import com.fthlbot.discordbotfthl.core.Annotation.CommandType;
import com.fthlbot.discordbotfthl.core.Annotation.Invoker;
import com.fthlbot.discordbotfthl.core.Handlers.Command;
import org.javacord.api.event.interaction.SlashCommandCreateEvent;
import org.javacord.api.interaction.callback.InteractionOriginalResponseUpdater;
import org.javacord.api.util.logging.ExceptionLogger;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.CompletableFuture;

@Component
@Invoker(
        alias = "minion-bot-string",
        description = "Get the string for the minion bot",
        usage = "/minion-bot-string",
        type = CommandType.STAFF
)
public class MinionBotStatsStringImpl implements Command {
    private final Logger log = LoggerFactory.getLogger(MinionBotStatsStringImpl.class);

    private final String COMMAND_PREFIX = "setmatch ";
    private final DivisionService divisionService;
    private final ScheduleWarService scheduleWarService;
    private final DivisionWeekService divisionWeekService;

    public MinionBotStatsStringImpl(
            DivisionService divisionService,
            ScheduleWarService scheduleWarService,
            DivisionWeekService divisionWeekService
    ) {
        this.divisionService = divisionService;
        this.scheduleWarService = scheduleWarService;
        this.divisionWeekService = divisionWeekService;
    }

    @Override
    public void execute(SlashCommandCreateEvent event) {
        try {
            log.info("I am working i think! getting response");
            CompletableFuture<InteractionOriginalResponseUpdater> respond = event.getSlashCommandInteraction().respondLater();

            int divisionId = Integer.parseInt(event.getSlashCommandInteraction().getArguments().get(0).getStringValue().get());

            DivisionWeeks divWeek = divisionWeekService.getDivWeekByID(divisionId);

            List<ScheduledWar> schedule = scheduleWarService.getScheduleByDivisionWeek(divWeek);

            StringBuilder sb = new StringBuilder();
            sb.append(COMMAND_PREFIX).append(" ");
            for (ScheduledWar war : schedule) {
                log.info("I am working!");
                String tag = war.getTeamA().getTag();
                String tag1 = war.getTeamB().getTag();
                sb.append(tag).append(" ").append(tag1).append(" ");
            }
            respond.thenAccept(res -> {
                if (false) {
                    //split into multiple messages - 1000 char max
                    int i = 0;
                    res.setContent("Sending multiple...").update();
                    while (i < sb.length()) {
                        int end = i + 1000;
                        if (end > sb.length()) {
                            end = sb.length();
                        }
                        event.getSlashCommandInteraction().createFollowupMessageBuilder().setContent(sb.substring(i, end)).send();
                        i = end;
                    }
                } else {
                    res.setContent(sb.toString()).update().exceptionally(ExceptionLogger.get());
                }
            });
        } catch (EntityNotFoundException e) {
            GeneralService.leagueSlashErrorMessage(event, e);

        } catch (NumberFormatException e) {
            GeneralService.leagueSlashErrorMessage(event, "Invalid division id - Must be a number");
        }catch (Exception e) {
            log.error("Error sending message", e);
            GeneralService.leagueSlashErrorMessage(event, e);
        }
    }
}
