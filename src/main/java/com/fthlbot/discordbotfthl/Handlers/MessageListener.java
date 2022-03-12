package com.fthlbot.discordbotfthl.Handlers;

import org.javacord.api.event.interaction.SlashCommandCreateEvent;
import org.javacord.api.event.message.MessageCreateEvent;
import org.javacord.api.listener.interaction.SlashCommandCreateListener;
import org.javacord.api.listener.message.MessageCreateListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import java.util.Locale;
import java.util.concurrent.CompletableFuture;

public class MessageListener implements SlashCommandCreateListener {
    private final Logger log = LoggerFactory.getLogger(MessageListener.class);
    private final MessageHolder messageHolder;

    public MessageListener(MessageHolder messageHolder) {
        this.messageHolder = messageHolder;
    }

    /*@Override
    public void onMessageCreate(MessageCreateEvent event) {
        if (!event.getMessageAuthor().isRegularUser()){
            return;
        }
        String[] args = event.getMessageContent().split("\\s+");
        String key = args[0].toLowerCase(Locale.ROOT);
        if (messageHolder.getCommand().containsKey(key)) {
            CompletableFuture.runAsync(() -> {
                Command command = messageHolder.getCommand().get(key);
                command.execute(event);
            });
        }
    }*/

    @Override
    public void onSlashCommandCreate(SlashCommandCreateEvent event) {
        String commandName = event.getSlashCommandInteraction().getCommandName();
        if (messageHolder.getCommand().containsKey(commandName)){
            CompletableFuture.runAsync(() -> {
                log.info("found a command {}", commandName);
                Command command = messageHolder.getCommand().get(commandName);
                command.execute(event);
            });
        }
    }
}
