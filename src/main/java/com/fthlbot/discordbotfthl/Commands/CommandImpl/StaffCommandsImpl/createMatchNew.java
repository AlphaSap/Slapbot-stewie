package com.fthlbot.discordbotfthl.Commands.CommandImpl.StaffCommandsImpl;

import com.fthlbot.discordbotfthl.DatabaseModels.ScheduleWar.DivisonWeek.DivisionWeeks;
import com.fthlbot.discordbotfthl.Util.Utils;
import com.fthlbot.discordbotfthl.core.Annotation.CommandType;
import com.fthlbot.discordbotfthl.core.Annotation.Invoker;
import com.fthlbot.discordbotfthl.core.Handlers.Command;
import org.javacord.api.event.interaction.SlashCommandCreateEvent;
import org.javacord.api.interaction.SlashCommandInteractionOption;
import org.javacord.api.interaction.callback.InteractionOriginalResponseUpdater;
import org.json.JSONArray;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;


@Invoker(
        alias = "create-match-up-new",
        type = CommandType.STAFF,
                                                    //2022-08-19
        usage = "/create-match-up-new <division> <div-week (Autocomplete)> <parseable string>",
        description = "Will create match ups with the parsable string used in /parse-string command!"
)
public class createMatchNew implements Command {
    private final Utils utils;

    public createMatchNew(Utils utils) {
        this.utils = utils;
    }

    @Override
    public void execute(SlashCommandCreateEvent event) {
        CompletableFuture<InteractionOriginalResponseUpdater> respondLater = event.getSlashCommandInteraction().respondLater();

        List<SlashCommandInteractionOption> arguments = event.getSlashCommandInteraction().getArguments();

        Optional<String> dateString = arguments.get(0).getStringValue();
        Date parse;
        try {
            parse = new SimpleDateFormat("yyyy-MM-dd").parse(dateString.get());
        } catch (ParseException e) {
            respondLater.thenAccept(res -> res.setContent("Enter a valid date, Format: yyyy-MM-dd"));
           return;
        }
        Optional<String> matchUpString = arguments.get(1).getStringValue();

        String parse1 = parse(matchUpString.get());

        JSONArray job = utils.getJsonArray(event.getApi(), respondLater, parse1);


    }
    private String parse(String input) {
        //input String : 1550v1212 3593v4174 4508v5153 4801v1242 4468v1759
        // parse the above input string to json string.
        // example output : [{home:1550, enemy:1212}, {home:3593, enemy:4174}, {home:4508, enemy:5153}, {home:4801, enemy:1242}, {home:4468, enemy:1759}]
        String[] teams = input.split("\\s+");
        StringBuilder sb = new StringBuilder();
        sb.append("[");
        for (String team : teams) {
            String[] teamSplit = team.split("v");
            sb.append("{");
            sb.append("\"home\":\"").append(teamSplit[0].trim()).append("\",");
            sb.append("\"enemy\":\"").append(teamSplit[1].trim()).append("\"");
            sb.append("},");
        }
        sb.deleteCharAt(sb.length() - 1);
        sb.append("]");
        return sb.toString();
    }

    private void createMatchUps(DivisionWeeks weeks, JSONArray job) {

    }
}
