package com.fthlbot.discordbotfthl.Handlers;

import com.fthlbot.discordbotfthl.Annotation.AllowedChannel;
import com.fthlbot.discordbotfthl.Annotation.CommandType;
import com.fthlbot.discordbotfthl.Annotation.Invoker;
import com.fthlbot.discordbotfthl.DatabaseModels.CommandLogger.CommandLogger;
import com.fthlbot.discordbotfthl.DatabaseModels.CommandLogger.CommandLoggerService;
import com.fthlbot.discordbotfthl.Util.BotConfig;
import org.javacord.api.DiscordApi;
import org.javacord.api.entity.message.MessageFlag;
import org.javacord.api.entity.permission.Role;
import org.javacord.api.entity.server.Server;
import org.javacord.api.entity.user.User;
import org.javacord.api.event.interaction.SlashCommandCreateEvent;
import org.javacord.api.interaction.SlashCommandInteraction;
import org.javacord.api.interaction.callback.InteractionOriginalResponseUpdater;
import org.javacord.api.listener.interaction.SlashCommandCreateListener;

import java.lang.annotation.Annotation;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
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
                    DiscordApi api = event.getApi();

                    User user = event.getSlashCommandInteraction().getUser();
                    boolean b =  user.isBotOwner() || hasStaffRole(user);
                    if (!b) {
                        CompletableFuture<InteractionOriginalResponseUpdater> respondLater = event.getSlashCommandInteraction().respondLater();
                        respondLater.thenAccept(res -> {
                            res.setContent("This command is restricted to staff only!").update();
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
                if (invoker.where().equals(AllowedChannel.APPLICANT_SERVER)){
                    return server.getId() == config.getApplicantServerID();
                }
                if (invoker.where().equals(AllowedChannel.NEGO_SERVER)){
                    return server.getId() == config.getNegoServerID();
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
    private boolean hasStaffRole(User user){
        List<Long> staffID = List.of(
                444574761121611788L,
                123094716014264322L,
                613121864016986112L,
                690562682784317470L,
                398424326723862529L,
                498175857244766218L,
                613121864016986112L,
                777114883984195605L,
                726808764056993833L,
                602935588018061453L,
                450224783972499467L);
        return staffID.stream().anyMatch(x -> x.equals(user.getId()));
        //return roles.stream().anyMatch(x -> x.getId() == config.getFthlServerStaffRoleID());
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
