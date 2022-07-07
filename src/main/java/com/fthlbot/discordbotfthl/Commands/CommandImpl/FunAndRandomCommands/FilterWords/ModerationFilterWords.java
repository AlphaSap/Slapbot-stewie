package com.fthlbot.discordbotfthl.Commands.CommandImpl.FunAndRandomCommands.FilterWords;

import com.fthlbot.discordbotfthl.Util.BotConfig;
import org.javacord.api.event.message.MessageCreateEvent;
import org.javacord.api.listener.message.MessageCreateListener;
import org.springframework.stereotype.Component;

import java.util.logging.Logger;

@Component
public class ModerationFilterWords implements MessageCreateListener {
    private static final Logger log = Logger.getLogger(ModerationFilterWords.class.getName());
    private final BotConfig botConfig;
    private final FilterWordService filterWordService;

    public ModerationFilterWords(BotConfig botConfig, FilterWordService filterWordService) {
        this.botConfig = botConfig;
        this.filterWordService = filterWordService;
    }

    @Override
    public void onMessageCreate(MessageCreateEvent event) {
        if (!event.getMessageAuthor().isRegularUser()) return;

        if (event.getServer().isEmpty()) return;

        if (event.getServer().get().getId() != botConfig.getFthlServerID()) return;

        log.info("Checking message: " + event.getMessageContent());
        if (filterWordService.checkMessage(event.getMessage().getContent())){
            filterWordService.timeOutUser(event.getApi(), event.getMessage().getAuthor().asUser().get(), event.getServer().get());
            event.getMessage().delete("Banned Word");
        }
    }


}
