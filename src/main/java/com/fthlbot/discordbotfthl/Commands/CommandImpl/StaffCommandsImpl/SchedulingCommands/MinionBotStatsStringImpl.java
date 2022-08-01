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
        CompletableFuture<InteractionOriginalResponseUpdater> respond = event.getSlashCommandInteraction().respondLater();
        int divisionId = event.getSlashCommandInteraction().getArguments().get(0).getLongValue().get().intValue();

        DivisionWeeks divWeek;
        try {
            divWeek = divisionWeekService.getDivWeekByID(divisionId);
        } catch (EntityNotFoundException e) {
            GeneralService.leagueSlashErrorMessage(event, e);
            return;
        }

        List<ScheduledWar> schedule;
        try {
            schedule = scheduleWarService.getScheduleByDivisionWeek(divWeek);
        } catch (EntityNotFoundException e) {
            GeneralService.leagueSlashErrorMessage(event, e);
            return;
        }

        StringBuilder sb = new StringBuilder();
        sb.append(COMMAND_PREFIX);
        for (ScheduledWar war : schedule) {
            String tag = war.getTeamA().getTag();
            String tag1 = war.getTeamB().getTag();
            sb.append(tag).append(" ").append(tag1);
        }
        respond.thenAccept(res ->{
           if (sb.length() > 2000){
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
           }else {
               res.setContent(sb.toString()).update();
           }
        });
    }
}
