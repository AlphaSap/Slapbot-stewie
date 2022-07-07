package com.fthlbot.discordbotfthl.Commands.CommandImpl.FunAndRandomCommands.FilterWords;

import com.fthlbot.discordbotfthl.Util.BotConfig;
import org.javacord.api.entity.message.MessageAuthor;
import org.javacord.api.event.message.MessageEditEvent;
import org.javacord.api.listener.message.MessageEditListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class FilterWordsEdit implements MessageEditListener {
    private final FilterWordService filterWordService;
    private final BotConfig botConfig;

    private final Logger log = LoggerFactory.getLogger(FilterWordsEdit.class.getName());

    public FilterWordsEdit(FilterWordService filterWordService, BotConfig botConfig) {
        this.filterWordService = filterWordService;
        this.botConfig = botConfig;
    }

    @Override
    public void onMessageEdit(MessageEditEvent event) {

        Optional<MessageAuthor> messageAuthor = event.getMessageAuthor();
        if (messageAuthor.isEmpty()) return;

        if (!messageAuthor.get().isRegularUser()) return;

        if (event.getServer().isEmpty()) return;

        if (event.getServer().get().getId() != botConfig.getFthlServerID()) return;

        log.info("Checking message: " + event.getMessageContent());

        if (filterWordService.checkMessage(event.getNewContent())) {
            filterWordService.timeOutUser(event.getApi(), messageAuthor.get().asUser().get(), event.getServer().get());
            event.deleteMessage();
        }
    }
}
