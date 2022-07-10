package com.fthlbot.discordbotfthl.Commands.CommandImpl.LeagueCommandsImpl.UtilCommands;

import com.fthlbot.discordbotfthl.core.Annotation.CommandType;
import com.fthlbot.discordbotfthl.core.Annotation.Invoker;
import com.fthlbot.discordbotfthl.Commands.CommandListener.ShowDivisionWeekListener;
import com.fthlbot.discordbotfthl.DatabaseModels.Division.Division;
import com.fthlbot.discordbotfthl.DatabaseModels.Division.DivisionService;
import com.fthlbot.discordbotfthl.DatabaseModels.Exception.LeagueException;
import com.fthlbot.discordbotfthl.DatabaseModels.ScheduleWar.DivisonWeek.DivisionWeekService;
import com.fthlbot.discordbotfthl.DatabaseModels.ScheduleWar.DivisonWeek.DivisionWeeks;
import com.fthlbot.discordbotfthl.Util.GeneralService;
import com.fthlbot.discordbotfthl.Util.Pagination.Pagination;
import org.javacord.api.entity.message.embed.EmbedBuilder;
import org.javacord.api.event.interaction.SlashCommandCreateEvent;
import org.javacord.api.interaction.callback.InteractionOriginalResponseUpdater;
import org.springframework.stereotype.Component;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

@Component
@Invoker(alias = "show-divisionweek",
        description = "Shows the current division week.",
        usage = "show-divisionweek <DIVISION>",
        type = CommandType.SCHEDULE
)
public class ShowDivisionWeekImpl implements ShowDivisionWeekListener {
    private final DivisionWeekService divisionWeekService;
    private final DivisionService divisionService;
    public ShowDivisionWeekImpl(DivisionWeekService divisionWeekService, DivisionService divisionService) {
        this.divisionWeekService = divisionWeekService;
        this.divisionService = divisionService;
    }

    @Override
    public void execute(SlashCommandCreateEvent event) {
        //get slashcommand respond later object
        CompletableFuture<InteractionOriginalResponseUpdater> response = event.getSlashCommandInteraction().respondLater();
        //get the division from arguments
        String division = event.getSlashCommandInteraction().getArguments().get(0).getStringValue().get();
        //get the division object
        Division divisionObject;
        List<DivisionWeeks> divisionWeeks;
        try {
            divisionObject = divisionService.getDivisionByAlias(division);
            divisionWeeks = divisionWeekService.getDivisionWeeksByDivision(divisionObject);
        } catch (LeagueException e) {
            //send send error message to discord, with the GeneralService.class
            GeneralService.leagueSlashErrorMessage(event, e);
            return;
        }

        //paginate the division weeks, each pagination should have a max of 10 weeks

        List<EmbedBuilder> embedBuilders = new ArrayList<>();
        for (int i = 0; i < divisionWeeks.size() ; i++) {
            embedBuilders.add(new EmbedBuilder()
                    .setTitle("Division Week")
                            .addInlineField("Week", divisionWeeks.get(i).getWeekNumber().toString())
                            .addInlineField("ID", divisionWeeks.get(i).getID().toString())
                            .addInlineField("Division", divisionObject.getAlias())
                            .addInlineField("Start Date", GeneralService.dateToStringInDiscordFormat(divisionWeeks.get(i).getWeekStartDate()) + "")
                            .addInlineField("End Date", GeneralService.dateToStringInDiscordFormat(divisionWeeks.get(i).getWeekEndDate()) + "")
                    .setColor(Color.GREEN)
                    .setTimestampToNow());
        }
        new Pagination().buttonPagination(embedBuilders, response, event.getApi());
    }
}
