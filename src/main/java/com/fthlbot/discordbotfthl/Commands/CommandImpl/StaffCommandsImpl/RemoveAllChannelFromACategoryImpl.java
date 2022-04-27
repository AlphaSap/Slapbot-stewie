package com.fthlbot.discordbotfthl.Commands.CommandImpl.StaffCommandsImpl;

import com.fthlbot.discordbotfthl.Annotation.CommandType;
import com.fthlbot.discordbotfthl.Annotation.Invoker;
import com.fthlbot.discordbotfthl.Commands.CommandListener.RemoveAllChannelFromACategoryListener;
import com.fthlbot.discordbotfthl.Util.BotConfig;
import org.javacord.api.entity.channel.Channel;
import org.javacord.api.entity.channel.ChannelCategory;
import org.javacord.api.entity.channel.RegularServerChannel;
import org.javacord.api.entity.channel.ServerTextChannel;
import org.javacord.api.entity.message.Message;
import org.javacord.api.entity.message.MessageBuilder;
import org.javacord.api.entity.message.component.ActionRow;
import org.javacord.api.entity.message.component.LowLevelComponent;
import org.javacord.api.entity.message.embed.EmbedBuilder;
import org.javacord.api.entity.server.Server;
import org.javacord.api.event.interaction.SlashCommandCreateEvent;
import org.javacord.api.interaction.callback.InteractionOriginalResponseUpdater;
import org.javacord.api.util.logging.ExceptionLogger;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.javacord.api.entity.message.component.Button;


import java.awt.Color;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

@Component
@Invoker(
        alias = "remove-channels-from-category",
        usage = "/remove-channels-from-category <Category ID> <Preserve Logs(True by default)>",
        description = "Removes all the channels from a category and preserves logs",
        type = CommandType.STAFF
)
public class RemoveAllChannelFromACategoryImpl implements RemoveAllChannelFromACategoryListener {
    private final BotConfig botConfig;
    private final Logger log = LoggerFactory.getLogger(RemoveAllChannelFromACategoryImpl.class);

    public RemoveAllChannelFromACategoryImpl(BotConfig botConfig) {
        this.botConfig = botConfig;
    }

    @Override
    public void execute(SlashCommandCreateEvent event) {

        CompletableFuture<InteractionOriginalResponseUpdater> respondLater = event.getSlashCommandInteraction().respondLater();

        String categoryID = event.getSlashCommandInteraction().getArguments().get(0).getStringValue().get();
        Boolean isLogs = true;
        if (event.getSlashCommandInteraction().getArguments().size() > 1) {
            if (event.getSlashCommandInteraction().getArguments().get(1).getBooleanValue().isPresent()) {
                isLogs = event.getSlashCommandInteraction().getArguments().get(1).getBooleanValue().get();
            }
        }

        Channel logChannel = event.getApi().getChannelById(botConfig.getErrorLogChannelID()).get();

        Optional<Server> server = event.getSlashCommandInteraction().getServer();
        if (server.isEmpty()) {
            respondLater.thenAccept(res -> {
                res.setContent("This command can be used inside a server!").update();
            });
            return;
        }

        Optional<ChannelCategory> category = event.getApi().getChannelCategoryById(categoryID);
        if (category.isEmpty()) {
            respondLater.thenAccept(res -> {
                res.setContent("Unable to find a category with the id: %s".formatted(categoryID)).update();
            }).exceptionally(ExceptionLogger.get());
            return;
        }
        List<RegularServerChannel> channels = category.get().getChannels();

        Boolean finalIsLogs = isLogs;
        respondLater.thenAccept(res -> {
            EmbedBuilder embedBuilder = new EmbedBuilder()
                    .setTitle("Do you want to remove all channels from the category: %s?".formatted(category.get().getName()))
                    .setColor(Color.RED)
                    .setDescription("This action is irreversible!");
            LowLevelComponent[] lowLevelComponents = {
                    Button.primary("Accept", "✅"),
                    Button.danger("Cancel", "❌")
            };
            res.addEmbed(embedBuilder).addComponents(ActionRow.of(lowLevelComponents)).update().thenAccept(message -> {
                message.addButtonClickListener(button -> {
                    if (button.getButtonInteraction().getCustomId().equals("Accept")) {
                        removeChannels(finalIsLogs, logChannel, channels);
                        event.getSlashCommandInteraction().createFollowupMessageBuilder().setContent("Removed all channels from the category: %s".formatted(category.get().getName()))
                                .send();
                        res.removeAllComponents().update();
                    } else if (button.getButtonInteraction().getCustomId().equals("Cancel")) {
                        event.getSlashCommandInteraction().createFollowupMessageBuilder().setContent("Cancelled!").send();
                        res.removeAllComponents().update();
                    }
                });
            }).exceptionally(ExceptionLogger.get()).join();
        }).exceptionally(ExceptionLogger.get()).join();
    }

    private void removeChannels(Boolean isLogs, Channel logChannel, List<RegularServerChannel> channels) {
        for (RegularServerChannel channel : channels) {
            log.info("Removing channel: {}", channel.getName());
            Optional<ServerTextChannel> serverTextChannel = channel.asServerTextChannel();
            if (serverTextChannel.isPresent()) {
                if (isLogs) {
                    File file = getFile(serverTextChannel.get());
                    sendLogsFileToLogChannel(file, logChannel.asServerTextChannel().get(), serverTextChannel.get());
                }
                channel.delete();
            }
        }
    }

    private void sendLogsFileToLogChannel(File file, ServerTextChannel LogChannel, ServerTextChannel negoChannel) {
        String msg = "%s".formatted(negoChannel.getName());
        LogChannel.sendMessage(msg, file);
    }

    private File getFile(ServerTextChannel channel) {
        List<String> messages = channel.getMessagesAsStream()
                .map(msg -> {
                    return "%s -> \"%s\"".formatted(msg.getAuthor().getDiscriminatedName(), msg.getContent());
                }).toList();
        log.warn("Found {} messages in channel {}", messages.size(), channel.getName());
        Path file = Paths.get("the-file-name.txt");
        try {
            Files.write(file, messages, StandardCharsets.UTF_8);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return file.toFile();
    }

}
