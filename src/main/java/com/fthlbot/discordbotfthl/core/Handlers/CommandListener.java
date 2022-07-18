package com.fthlbot.discordbotfthl.core.Handlers;

import com.fthlbot.discordbotfthl.core.Annotation.AllowedChannel;
import com.fthlbot.discordbotfthl.core.Annotation.CommandType;
import com.fthlbot.discordbotfthl.core.Annotation.Invoker;
import com.fthlbot.discordbotfthl.DatabaseModels.CommandLogger.CommandLogger;
import com.fthlbot.discordbotfthl.DatabaseModels.CommandLogger.CommandLoggerService;
import com.fthlbot.discordbotfthl.Util.BotConfig;
import org.javacord.api.DiscordApi;
import org.javacord.api.entity.message.MessageFlag;
import org.javacord.api.entity.server.Server;
import org.javacord.api.entity.user.User;
import org.javacord.api.event.interaction.SlashCommandCreateEvent;
import org.javacord.api.interaction.SlashCommandInteraction;
import org.javacord.api.interaction.callback.InteractionOriginalResponseUpdater;
import org.javacord.api.listener.interaction.SlashCommandCreateListener;

import java.lang.annotation.Annotation;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.concurrent.CompletableFuture;

public class CommandListener implements SlashCommandCreateListener {
    private final MessageHolder messageHolder;
    private final CommandLoggerService service;
    private final BotConfig config;

    public CommandListener(MessageHolder messageHolder, CommandLoggerService service, BotConfig config) {
        this.messageHolder = messageHolder;
        this.service = service;
        this.config = config;
    }


    @Override
    public void onSlashCommandCreate(SlashCommandCreateEvent event) {
        String commandName = event.getSlashCommandInteraction().getCommandName();
        if (messageHolder.getCommand().containsKey(commandName)){
            CompletableFuture.runAsync(() -> {
                Command command = messageHolder.getCommand().get(commandName);
                if (event.getSlashCommandInteraction().getServer().isEmpty()){
                    event.getSlashCommandInteraction().createImmediateResponder().setContent("Sorry I do not work in DMs!").respond();
                    return;
                }
                if (!canUserAnywhere(command, event.getSlashCommandInteraction().getServer().get())){
                    event.getSlashCommandInteraction()
                            .createImmediateResponder()
                            .setFlags(MessageFlag.EPHEMERAL)
                            .setContent(
                                    "Sorry this is not the correct the server to use this " +
                                            "command!\nPlease see my help page to find where you can use this command!"
                            ).respond();
                    return;
                }
                boolean staffCommand = isStaffCommand(command);
                if (staffCommand){
                    User user = event.getSlashCommandInteraction().getUser();
                    boolean b = user.isBotOwner() || hasStaffRole(user, event.getApi());
                    if (!b) {
                        CompletableFuture<InteractionOriginalResponseUpdater> respondLater = event.getSlashCommandInteraction().respondLater();
                        respondLater.thenAccept(res -> {
                            res.setContent("This command is restricted to staff only!").update();
                        });
                        return;
                    }
                }
                boolean devCommand = isDevCommand(command);
                if (devCommand){
                    User user = event.getSlashCommandInteraction().getUser();
                    boolean b = user.isBotOwner();
                    if (!b) {
                        CompletableFuture<InteractionOriginalResponseUpdater> respondLater = event.getSlashCommandInteraction().respondLater();
                        respondLater.thenAccept(res -> {
                            res.setContent("This command is restricted to developers only!").update();
                        });
                        return;
                    }
                }
                command.execute(event);
            });
            logCommand(event);
        }
        else{
            event.getSlashCommandInteraction().createImmediateResponder().setContent("Unable to locate this command").respond();
        }
    }

    private boolean canUserAnywhere(Command command, Server server){
        Annotation[] annotations = command.getClass().getAnnotations();
        for (Annotation annotation : annotations) {
            if (annotation instanceof Invoker invoker) {
                if (invoker.where().equals(AllowedChannel.ANYWHERE)){
                    return true;
                }
                else if (invoker.where().equals(AllowedChannel.APPLICANT_SERVER)){
                    return server.getId() == config.getApplicantServerID();
                }
                else if (invoker.where().equals(AllowedChannel.NEGO_SERVER)){
                    return server.getId() == config.getNegoServerID();
                }
                else if (invoker.where().equals(AllowedChannel.MAIN_SERVER)){
                    return server.getId() == config.getFthlServerID();
                }
            }
        }
        return false;
    }

    private boolean isStaffCommand(Command command) {
        Annotation[] annotations = command.getClass().getAnnotations();
        for (Annotation annotation : annotations) {
            if (annotation instanceof Invoker invoker){
                if (invoker.type().equals(CommandType.STAFF)){
                    return true;
                }
            }
        }
        return false;
    }
    private boolean isDevCommand(Command command) {
        Annotation[] annotations = command.getClass().getAnnotations();
        for (Annotation annotation : annotations) {
            if (annotation instanceof Invoker invoker){
                if (invoker.type().equals(CommandType.DEV)){
                    return true;
                }
            }
        }
        return false;
    }
    private boolean hasStaffRole(User user, DiscordApi api){
        return api.getServerById(config.getFthlServerID()).get()
                .getRoles(user)
                .stream()
                .anyMatch(x -> x.getId() == config.getFthlServerStaffRoleID());
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
