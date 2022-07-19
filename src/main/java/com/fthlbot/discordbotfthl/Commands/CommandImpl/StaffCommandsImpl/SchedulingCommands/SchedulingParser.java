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
 * @since 2.0
 * @version 2.0
 * @Notes: This class is used to parse the scheduling commands.
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
) @Component
public class SchedulingParser  implements Command {

    @Override
    public void execute(SlashCommandCreateEvent event) {
        CompletableFuture<InteractionOriginalResponseUpdater> response = event.getSlashCommandInteraction().respondLater();

        String toParseString = event.getSlashCommandInteraction().getArguments().get(0).getStringValue().get();

        StringBuilder sb = parseStringToJson(toParseString);
        response.thenAccept(r -> r.setContent("```" + sb.toString() + "```").update());

    }

    public StringBuilder parseStringToJson(String toParseString) {
        String[] toParse = toParseString.split("\n");

        StringBuilder sb = new StringBuilder();
        sb.append("[");
        for (String team : toParse) {
            String[] teamSplit = team.split("v");
            sb.append("{");
            sb.append("\"home\":\"").append(teamSplit[0].trim()).append("\",");
            sb.append("\"enemy\":\"").append(teamSplit[1].trim()).append("\"");
            sb.append("},");
        }
        sb.deleteCharAt(sb.length() - 1);
        sb.append("]");

        return sb;
    }
}
