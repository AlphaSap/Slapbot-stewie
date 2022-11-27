package com.fthlbot.discordbotfthl.Commands.CommandImpl.StaffCommandsImpl.SchedulingCommands;

import com.fthlbot.discordbotfthl.core.Annotation.Invoker;
import com.fthlbot.discordbotfthl.core.Handlers.Command;
import org.javacord.api.event.interaction.SlashCommandCreateEvent;
import org.javacord.api.interaction.callback.InteractionOriginalResponseUpdater;
import org.springframework.stereotype.Component;

import java.util.concurrent.CompletableFuture;

import static com.fthlbot.discordbotfthl.core.Annotation.AllowedChannel.*;
import static com.fthlbot.discordbotfthl.core.Annotation.CommandType.*;

/**
 * @author Sahil
 * @version 2.0
 * @Notes: This class is used to parse the scheduling commands.
 * @since 2.0
 */


/**
 * Sample usage:
 * /parse-schedule <Text></>
 *
 * text example:
 * TEAMID v TEAMID
 * TEAMID v TEAMID
 * ...
 *
 * ..
 *
 * this text is converted to a json string.
 *
 * firstly we will separate everything by "\n" and then we will separate each team by "v" trimming the spaces.
 */
@Invoker(
        alias = "parse-schedule",
        description = "Parses the schedule, enter the text as  Text example:\n" +
                "TEAMID v TEAMID\n" +
                "TEAMID v TEAMID",

        usage = "/parse-schedule <TEXT>",
        type = STAFF,
        where = NEGO_SERVER
)
@Component
public class SchedulingParser implements Command {

    @Override
    public void execute(SlashCommandCreateEvent event) {
        CompletableFuture<InteractionOriginalResponseUpdater> response = event.getSlashCommandInteraction().respondLater();

        String toParseString = event.getSlashCommandInteraction().getArguments().get(0).getStringValue().get();

        try {
            String sb = parse(toParseString);
            response.thenAccept(r -> r.setContent("```" + sb.toString() + "```").update());
        }catch (ArrayIndexOutOfBoundsException e) {
            response.thenAccept(r -> r.setContent("""
                    ```Please enter the text as example:
                    TEAM_IDvTEAM_ID
                    TEAM_IDvTEAM_ID```""").update());
        }catch (Exception e) {
            response.thenAccept(r -> r.setContent("```" + "Error: " + e.getMessage() + "```").update());
        }
    }

    public String parse(String input) {
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
}
