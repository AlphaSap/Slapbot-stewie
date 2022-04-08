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
import org.javacord.api.event.interaction.SlashCommandCreateEvent;
import org.json.JSONArray;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

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
    private final Logger log = LoggerFactory.getLogger(AddDivisionWeeksImpl.class);
    public AddDivisionWeeksImpl(DivisionService divisionService, DivisionWeekService divisionWeekService, BotConfig config) {
        this.divisionService = divisionService;
        this.divisionWeekService = divisionWeekService;
        this.config = config;
    }

    @Override
    public void execute(SlashCommandCreateEvent event) {
        //TODO replace with user input
        String json;
        //TDOO regex for discord attachment link https://cdn.discordapp.com/attachments/[0-9]+/[0-9]+/[a-zA-Z\.]+
        try {
            json = GeneralService.getFileContent("sampleJSONforf5Week.json");
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }
        Division division = null;
        try {
            division = divisionService.getDivisionByAlias("f5");
        } catch (EntityNotFoundException e) {
            e.printStackTrace();
        }

        JSONArray s = new JSONArray(json);

        int length = s.length();
        List<DivisionWeeks> divisionWeekList = new ArrayList<>();
        for (int i = 0; i < length; i++) {
            String start = s.getJSONObject(i).getString("start");
            Date date = null;
            try {
                date = new SimpleDateFormat("dd-MM-yyyy").parse(start);
            } catch (ParseException e) {
                e.printStackTrace();
            }

            String end = s.getJSONObject(i).getString("end");
            boolean isByeWeek = s.getJSONObject(i).getBoolean("byeWeek");
            Date date1 = null;
            try {
                date1 = new SimpleDateFormat("dd-MM-yyyy").parse(end);
            } catch (ParseException e) {
                e.printStackTrace();
            }

            DivisionWeeks divisionWeeks = new DivisionWeeks(i + 1, date, date1, division, isByeWeek );

        }
        divisionWeekService.save(divisionWeekList);
    }
}
