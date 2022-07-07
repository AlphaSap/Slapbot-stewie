package com.fthlbot.discordbotfthl.Commands.CommandImpl.FunAndRandomCommands.FilterWords;

import org.javacord.api.DiscordApi;
import org.javacord.api.entity.server.Server;
import org.javacord.api.entity.user.User;
import org.javacord.api.util.logging.ExceptionLogger;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.Duration;

@Service
public class FilterWordService {
    private static final Logger log = LoggerFactory.getLogger(FilterWordService.class.getName());
    public boolean checkMessage(String text) {
        String[] args = text.toLowerCase()
                .replace("╭╮", "n")
                .replace("9", "g")
                .replace("", "n")
                .replace("3", "e")
                .split("\\s+");

        for (String arg : args) {
            if(arg.matches("^:[A-za-z\\d]+:$")) continue;
            if (arg.matches("nigg")) return true;

            if (arg.startsWith("n") && arg.length() >= 5){
                if (arg.endsWith("gga") || arg.endsWith("gger")) return true;
            }

            if (arg.matches("n[i|*][g|*|q][g|*|q][e|*][r|*]") || arg.matches("n[i|*][g|*|q][g|*|q][a|*]") ) {
                if (arg.matches("^[*]+$")) continue;
                return true;
            }

            if (arg.matches("igga") || arg.matches("igger")) return true;
        }

        String join = String.join("", args).toLowerCase();
        if (join.startsWith("nigg")) return true;

        return false;
    }

    public void timeOutUser(DiscordApi api, User user, Server server){
        server.timeoutUser(user, Duration.ofHours(8)).exceptionally(ExceptionLogger.get());
        log.info("Timed out " + user.getName());
    }
}
