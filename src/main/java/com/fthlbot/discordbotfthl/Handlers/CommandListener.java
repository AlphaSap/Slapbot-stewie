package com.fthlbot.discordbotfthl.Handlers;

import com.fthlbot.discordbotfthl.Annotation.CommandType;
import com.fthlbot.discordbotfthl.Annotation.Invoker;
import com.fthlbot.discordbotfthl.DatabaseModels.CommandLogger.CommandLogger;
import com.fthlbot.discordbotfthl.DatabaseModels.CommandLogger.CommandLoggerService;
import com.fthlbot.discordbotfthl.Util.BotConfig;
import org.javacord.api.DiscordApi;
import org.javacord.api.entity.permission.Role;
import org.javacord.api.entity.server.Server;
import org.javacord.api.entity.user.User;
import org.javacord.api.event.interaction.SlashCommandCreateEvent;
import org.javacord.api.interaction.SlashCommandInteraction;
import org.javacord.api.interaction.callback.InteractionOriginalResponseUpdater;
import org.javacord.api.listener.interaction.SlashCommandCreateListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.annotation.Annotation;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class CommandListener implements SlashCommandCreateListener {
    private final Logger log = LoggerFactory.getLogger(CommandListener.class);
    private final MessageHolder messageHolder;
    private final CommandLoggerService service;
    private final BotConfig config;

    public CommandListener(MessageHolder messageHolder, CommandLoggerService service, BotConfig config) {
        this.messageHolder = messageHolder;
        this.service = service;
        this.config = config;
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
        log.info("Command is here!!!!!!");
        if (messageHolder.getCommand().containsKey(commandName)){
            CompletableFuture.runAsync(() -> {
                Command command = messageHolder.getCommand().get(commandName);
                boolean staffCommand = isStaffCommand(command);
                if (staffCommand){
                    long fthlServerID = config.getFthlServerID();
                    long testServerID = config.getTestServerID();
                    DiscordApi api = event.getApi();

                    Server server = api.getServerById(fthlServerID).orElse(api.getServerById(testServerID).get());
                    User user = event.getSlashCommandInteraction().getUser();
                    boolean b = hasStaffRole(server, user) || user.isBotOwner();
                    if (!b) {
                        CompletableFuture<InteractionOriginalResponseUpdater> respondLater = event.getSlashCommandInteraction().respondLater();
                        respondLater.thenAccept(res -> {
                            res.setContent("This command is restricted to staff only!").update();
                        });
                        return;
                    }
                }
                log.info(commandName + " " + command.getClass().getName());
                command.execute(event);
            });
            logCommand(event);
        }
        else{
            event.getSlashCommandInteraction().createImmediateResponder().setContent("Unable to locate this command").respond();
        }
    }

    private boolean isStaffCommand(Command command) {
      //  if (api.getOwnerId() == user.getId()) return true;

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
    private boolean hasStaffRole(Server server, User user){
        List<Role> roles = user.getRoles(server);
        return roles.stream().anyMatch(x -> x.getId() == config.getStaffRoleID());
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
