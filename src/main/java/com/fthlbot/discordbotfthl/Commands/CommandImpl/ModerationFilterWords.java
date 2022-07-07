package com.fthlbot.discordbotfthl.Commands.CommandImpl;

import com.fthlbot.discordbotfthl.Util.BotConfig;
import org.javacord.api.DiscordApi;
import org.javacord.api.entity.message.MessageAuthor;
import org.javacord.api.entity.server.Server;
import org.javacord.api.entity.user.User;
import org.javacord.api.event.message.MessageCreateEvent;
import org.javacord.api.event.message.MessageEditEvent;
import org.javacord.api.listener.message.MessageCreateListener;
import org.javacord.api.listener.message.MessageEditListener;
import org.javacord.api.util.logging.ExceptionLogger;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.Optional;
import java.util.logging.Logger;

@Component
public class ModerationFilterWords implements MessageCreateListener, MessageEditListener {
    private static final Logger log = Logger.getLogger(ModerationFilterWords.class.getName());
    private final BotConfig botConfig;

    public ModerationFilterWords(BotConfig botConfig) {
        this.botConfig = botConfig;
    }

    @Override
    public void onMessageCreate(MessageCreateEvent event) {
        if (!event.getMessageAuthor().isRegularUser()) return;

        if (event.getServer().isEmpty()) return;

        if (event.getServer().get().getId() != botConfig.getFthlServerID()) return;

        if (checkMessage(event.getMessage().getContent())){
            timeOutUser(event.getApi(), event.getMessage().getAuthor().asUser().get(), event.getServer().get());
            event.getMessage().delete("Banned Word");
        }
    }

    private boolean checkMessage(String text) {
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

    private void timeOutUser(DiscordApi api, User user, Server server){
        server.timeoutUser(user, Duration.ofHours(8)).exceptionally(ExceptionLogger.get());
        log.info("Timed out " + user.getName());
    }

    @Override
    public void onMessageEdit(MessageEditEvent event) {

        Optional<MessageAuthor> messageAuthor = event.getMessageAuthor();
        if (messageAuthor.isEmpty()) return;

        if (!messageAuthor.get().isRegularUser()) return;

        if (event.getServer().isEmpty()) return;

        if (event.getServer().get().getId() != botConfig.getFthlServerID()) return;

        if (checkMessage(event.getNewContent())) {
            timeOutUser(event.getApi(), messageAuthor.get().asUser().get(), event.getServer().get());
            event.deleteMessage();
        }
    }
}
