package com.fthlbot.discordbotfthl.Handlers;

import com.fthlbot.discordbotfthl.DatabaseModels.CommandLogger.CommandLogger;
import com.fthlbot.discordbotfthl.DatabaseModels.CommandLogger.CommandLoggerService;
import org.javacord.api.event.interaction.SlashCommandCreateEvent;
import org.javacord.api.interaction.SlashCommandInteraction;
import org.javacord.api.interaction.SlashCommandInteractionOption;
import org.javacord.api.listener.interaction.SlashCommandCreateListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class MessageListener implements SlashCommandCreateListener {
    private final Logger log = LoggerFactory.getLogger(MessageListener.class);
    private final MessageHolder messageHolder;
    private final CommandLoggerService service;

    public MessageListener(MessageHolder messageHolder, CommandLoggerService service) {
        this.messageHolder = messageHolder;
        this.service = service;
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
                Command command = messageHolder.getCommand().get(commandName);
                command.execute(event);
                CompletableFuture.runAsync(() -> {
                    logCommand(event);
                });
            });
        }
    }

    private void logCommand(SlashCommandCreateEvent event) {
        SlashCommandInteraction slashCommandInteraction = event.getSlashCommandInteraction();
        long serverID = slashCommandInteraction.getServer().isPresent() ? slashCommandInteraction.getServer().get().getId() : 0L;
        long channelID = slashCommandInteraction.getChannel().isPresent() ? slashCommandInteraction.getChannel().get().getId() : 0L;
        CommandLogger logger = new CommandLogger(
                slashCommandInteraction.getCommandId(),
                slashCommandInteraction.getUser().getId(),
                channelID,
                slashCommandInteraction.getCommandName(),
                null,
                LocalDateTime.ofInstant(slashCommandInteraction.getCreationTimestamp(), ZoneId.of("UTC")),
                serverID
        );
        service.logCommand(logger);
    }
}
