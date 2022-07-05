package com.fthlbot.discordbotfthl.Commands.CommandImpl;

import com.fthlbot.discordbotfthl.Util.BotConfig;
import org.javacord.api.DiscordApi;
import org.javacord.api.entity.permission.PermissionType;
import org.javacord.api.entity.server.Server;
import org.javacord.api.entity.user.User;
import org.javacord.api.event.message.MessageCreateEvent;
import org.javacord.api.listener.message.MessageCreateListener;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.logging.Logger;

@Component
public class ModerationFilterWords implements MessageCreateListener {
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
        }
    }

    private boolean checkMessage(String text) {
        String[] args = text.toLowerCase().split("nigger");

        for (String arg : args) {
            if (arg.matches("nigg")) return true;

            if (arg.startsWith("n") && arg.length() >= 5){
                if (arg.endsWith("gga") || arg.endsWith("gger")) return true;
            }

            if (arg.matches("n[i|*]gger") || arg.matches("n[i|*]gga") ) return true;
        }

        String join = String.join("", args).toLowerCase();
        if (join.startsWith("nigg")) return true;

        return false;
    }

    private void timeOutUser(DiscordApi api, User user, Server server){
        boolean permission = server.hasAnyPermission(api.getYourself(), PermissionType.MODERATE_MEMBERS);

        if (!permission) {
            log.info("Tried to Time out a member but do not have permission!");
            return;
        }

        server.timeoutUser(user, Duration.ofHours(8));
        log.info("Timed out " + user.getName());
    }
}
