package com.fthlbot.discordbotfthl.Commands.CommandImpl.FunAndRandomCommands.FilterWords;

import org.javacord.api.DiscordApi;
import org.javacord.api.entity.server.Server;
import org.javacord.api.entity.user.User;
import org.javacord.api.util.logging.ExceptionLogger;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class FilterWordService {
    private static final Logger log = LoggerFactory.getLogger(FilterWordService.class.getName());
    public boolean checkMessage(String text) {
        String[] args = getSplit(text);

        for (String arg : args) {
            if (checkString(arg)) return true;
        }

        for (String arg : args) {
            //remove all the identical letters from a string and group them in one letter
            Set<String> words = new HashSet<>();

            for (char c : arg.toCharArray()) {
                words.add(String.valueOf(c).toLowerCase());
            }
            //convert the hashset into a word
            String collect = words.stream().map(Object::toString).collect(Collectors.joining());
            //reverse the word and check if it is the same as the original word
            if (checkString(new StringBuilder(collect).reverse().toString())) return true;
        }

        String join = String.join("", args).toLowerCase();

        return join.startsWith("nigg");
    }

    private static boolean checkString(String arg) {
            //if(arg.matches("^:[A-za-z\\d]+:$")) continue;
            if (arg.matches("nigg")) return true;

            if (arg.matches("niger")) return true;

            if (arg.startsWith("n") && arg.length() >= 5){
                if (arg.endsWith("gga") || arg.endsWith("gger")) return true;
            }

            if (arg.matches("n[i|*][g|*|q][g|*|q][e|*][r|*]") || arg.matches("n[i|*][g|*|q][g|*|q][a|*]") ) {
                return !arg.matches("^[*]+$");
            }

            if (arg.matches("igga") || arg.matches("igger")) return true;
        return false;
    }

    private static String[] getSplit(String text) {
        return text.toLowerCase().replace("╭╮", "n")
                .replace("9", "g")
                .replace("3", "e")
                .replace("¡", "i")
                .replace("\uD835\uDE33", "e")
                .split("\\s+");
    }

    public void timeOutUser(DiscordApi api, User user, Server server){
        server.timeoutUser(user, Duration.ofHours(8)).exceptionally(ExceptionLogger.get()).exceptionally(ExceptionLogger.get());
        log.info("Timed out " + user.getName());
    }
}
