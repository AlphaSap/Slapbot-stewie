package com.fthlbot.discordbotfthl.Commands.CommandImpl;

import com.fthlbot.discordbotfthl.Annotation.CommandType;
import com.fthlbot.discordbotfthl.Annotation.Invoker;
import com.fthlbot.discordbotfthl.Commands.CommandListener.AddDivisionWeekListener;
import com.fthlbot.discordbotfthl.DatabaseModels.Division.Division;
import com.fthlbot.discordbotfthl.DatabaseModels.Division.DivisionService;
import com.fthlbot.discordbotfthl.DatabaseModels.Exception.EntityNotFoundException;
import com.fthlbot.discordbotfthl.DatabaseModels.ScheduleWar.DivisonWeek.DivisionWeekService;
import com.fthlbot.discordbotfthl.DatabaseModels.ScheduleWar.DivisonWeek.DivisionWeeks;
import com.fthlbot.discordbotfthl.Util.BotConfig;
import com.fthlbot.discordbotfthl.Util.GeneralService;
import com.fthlbot.discordbotfthl.Util.JavacordLogger;
import com.fthlbot.discordbotfthl.Util.Utils;
import org.javacord.api.event.interaction.SlashCommandCreateEvent;
import org.javacord.api.interaction.SlashCommandInteractionOption;
import org.javacord.api.interaction.callback.InteractionCallbackDataFlag;
import org.javacord.api.interaction.callback.InteractionOriginalResponseUpdater;
import org.json.JSONArray;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.CompletableFuture;

@Component
@Invoker(
        alias = "add-weeks",
        usage = "/add-weeks <DIVISION> <JSON STRING>",
        description = "A command to add weeks for a division",
        type = CommandType.STAFF
)
public class AddDivisionWeeksImpl implements AddDivisionWeekListener {
    private final DivisionService divisionService;
    private final DivisionWeekService divisionWeekService;
    private final BotConfig config;
    private final Utils utils;
    private final Logger log = LoggerFactory.getLogger(AddDivisionWeeksImpl.class);

    public AddDivisionWeeksImpl(DivisionService divisionService, DivisionWeekService divisionWeekService, BotConfig config, Utils utils) {
        this.divisionService = divisionService;
        this.divisionWeekService = divisionWeekService;
        this.config = config;
        this.utils = utils;
    }

    @Override
    public void execute(SlashCommandCreateEvent event) {
        JavacordLogger j = new JavacordLogger();
        j.setChannel(event.getApi().getServerTextChannelById(config.getErrorLogChannelID()).get());
        j.setLogger(this.getClass());
        //TODO replace with user input
        //TODO regex for discord attachment link https://cdn.discordapp.com/attachments/[0-9]+/[0-9]+/[a-zA-Z\.]+
        if (!event.getSlashCommandInteraction().getUser().isBotOwner()){
            event.getSlashCommandInteraction()
                    .createImmediateResponder()
                    .setFlags(InteractionCallbackDataFlag.EPHEMERAL)
                    .setContent("This command can only be used bot owner")
                    .respond();
            return;
        }
        List<SlashCommandInteractionOption> arguments = event.getSlashCommandInteraction().getArguments();
        CompletableFuture<InteractionOriginalResponseUpdater> respondLater = event.getSlashCommandInteraction().respondLater();
        String divAlias = arguments.get(0).getStringValue().get();
        String json = arguments.get(1).getStringValue().get();
        JSONArray s = null;

        s = utils.getJsonArray(event, respondLater, json);

        //Return because the error message will be sent via the getJsonArray method
        if (s == null) return;

        Division division = null;
        try {
            division = divisionService.getDivisionByAlias(divAlias);
        } catch (EntityNotFoundException e) {
            GeneralService.leagueSlashErrorMessage(respondLater, e);
            return;
        }

        int length = s.length();
        List<DivisionWeeks> divisionWeekList = new ArrayList<>();

        for (int i = 0; i < length; i++) {
            String start = s.getJSONObject(i).getString("start");
            Date date = null;

            String end = s.getJSONObject(i).getString("end");
            boolean isByeWeek = s.getJSONObject(i).getBoolean("byeWeek");
            Date date1 = null;
            try {
                date = new SimpleDateFormat("dd-MM-yyyy").parse(start);
                date1 = new SimpleDateFormat("dd-MM-yyyy").parse(end);
            } catch (ParseException e) {
                e.printStackTrace();
                log.error(e.getMessage());
                continue;
            }

            try {
                Date[] datesForDivision = config.getDateByDivision(division.getAlias());
                //TODO Validate the dates not as important, more like a quality of life change
            } catch (ParseException e) {
                e.printStackTrace();
                respondLater.thenAccept(res -> {
                    res.setContent("Unexpected error").update();
                    j.error("Cannot parse dates array, line 106", event.getSlashCommandInteraction().getUser(), event.getSlashCommandInteraction().getServer().get());
                });
                return;
            }

            DivisionWeeks divisionWeek = new DivisionWeeks(i + 1, date, date1, division, isByeWeek);
            divisionWeekList.add(divisionWeek);
        }
        divisionWeekService.save(divisionWeekList);
        respondLater.thenAccept(res -> {
            res.setContent("Weeks added successfully").update();
        });
        j.info("Dates added for the division: %s".formatted(division.getAlias()), event.getSlashCommandInteraction().getUser(), event.getSlashCommandInteraction().getServer().get());
    }
}
