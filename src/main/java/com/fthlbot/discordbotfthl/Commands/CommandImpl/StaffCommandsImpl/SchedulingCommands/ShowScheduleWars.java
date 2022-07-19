package com.fthlbot.discordbotfthl.Commands.CommandImpl.StaffCommandsImpl.SchedulingCommands;

import com.fthlbot.discordbotfthl.DatabaseModels.Exception.EntityNotFoundException;
import com.fthlbot.discordbotfthl.DatabaseModels.ScheduleWar.DivisonWeek.DivisionWeekService;
import com.fthlbot.discordbotfthl.DatabaseModels.ScheduleWar.DivisonWeek.DivisionWeeks;
import com.fthlbot.discordbotfthl.DatabaseModels.ScheduleWar.Schedule.ScheduleWarService;
import com.fthlbot.discordbotfthl.DatabaseModels.ScheduleWar.Schedule.ScheduledWar;
import com.fthlbot.discordbotfthl.Util.GeneralService;
import com.fthlbot.discordbotfthl.Util.Pagination.Pagination;
import com.fthlbot.discordbotfthl.core.Annotation.Invoker;
import com.fthlbot.discordbotfthl.core.Handlers.Command;
import org.javacord.api.entity.message.embed.EmbedBuilder;
import org.javacord.api.event.interaction.SlashCommandCreateEvent;
import org.javacord.api.interaction.callback.InteractionOriginalResponseUpdater;
import org.springframework.stereotype.Component;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import static com.fthlbot.discordbotfthl.core.Annotation.CommandType.SCHEDULE;

/**
 * @author Sahil
 * @version 2.0
 * @Notes: This class is used to show the schedule of the wars.
 * @since 2.0
 */

@Invoker(
        alias = "show-schedule-wars",
        description = "Shows the schedule of the wars.",
        usage = "/show-schedule-wars",
        type = SCHEDULE
)
@Component
public class ShowScheduleWars implements Command {

    private final ScheduleWarService scheduleWarService;
    private final DivisionWeekService divisionWeekService;

    public ShowScheduleWars(ScheduleWarService scheduleWarService, DivisionWeekService divisionWeekService) {
        this.scheduleWarService = scheduleWarService;
        this.divisionWeekService = divisionWeekService;
    }

    @Override
    public void execute(SlashCommandCreateEvent event) {
        CompletableFuture<InteractionOriginalResponseUpdater> response = event.getSlashCommandInteraction().respondLater();
        int i = event.getSlashCommandInteraction().getArguments().get(0).getLongValue().get().intValue();

        DivisionWeeks divWeekByID;
        try {
            divWeekByID = divisionWeekService.getDivWeekByID(i);
        } catch (EntityNotFoundException e) {
            GeneralService.leagueSlashErrorMessage(event, e);
            return;
        }

        List<ScheduledWar> scheduleByDivisionWeek;
        try {
            scheduleByDivisionWeek = scheduleWarService.getScheduleByDivisionWeek(divWeekByID);
        } catch (EntityNotFoundException e) {
            GeneralService.leagueSlashErrorMessage(event, e);
            return;
        }

        List<EmbedBuilder> embedBuilders = new ArrayList<>();
        for (int j = 0; j < scheduleByDivisionWeek.size(); j++) {
            ScheduledWar scheduledWar = scheduleByDivisionWeek.get(j);
            String start = GeneralService.dateToStringInDiscordFormat(scheduledWar.getDivisionWeeks().getWeekStartDate());
            String end = GeneralService.dateToStringInDiscordFormat(scheduledWar.getDivisionWeeks().getWeekEndDate());
            EmbedBuilder date = new EmbedBuilder()
                    .setTitle("Schedule of the " + scheduledWar.getDivisionWeeks().getDivision().getName() + " " + scheduledWar.getDivisionWeeks().getID())
                    .setDescription(scheduledWar.getTeamA().getName() + " vs " + scheduledWar.getTeamB().getName())
                    .addField("Date", start + " - " + end, false)
                    .setColor(Color.orange)
                    .setFooter("page " + (j + 1) + " of " + scheduleByDivisionWeek.size())
                    .setTimestampToNow();
            embedBuilders.add(date);
        }

        new Pagination().buttonPagination(embedBuilders, response, event.getApi());
    }
}
