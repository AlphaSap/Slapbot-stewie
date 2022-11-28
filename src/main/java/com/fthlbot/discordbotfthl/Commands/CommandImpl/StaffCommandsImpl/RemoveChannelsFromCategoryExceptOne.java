package com.fthlbot.discordbotfthl.Commands.CommandImpl.StaffCommandsImpl;

import com.fthlbot.discordbotfthl.Util.Pagination.ButtonRemoveJobScheduler;
import com.fthlbot.discordbotfthl.core.Annotation.AllowedChannel;
import com.fthlbot.discordbotfthl.core.Annotation.CommandType;
import com.fthlbot.discordbotfthl.core.Annotation.Invoker;
import com.fthlbot.discordbotfthl.core.Handlers.Command;
import org.javacord.api.entity.channel.ChannelCategory;
import org.javacord.api.entity.channel.RegularServerChannel;
import org.javacord.api.entity.message.Message;
import org.javacord.api.entity.message.component.ActionRow;
import org.javacord.api.entity.message.component.Button;
import org.javacord.api.entity.message.component.ButtonStyle;
import org.javacord.api.entity.message.embed.EmbedBuilder;
import org.javacord.api.entity.server.Server;
import org.javacord.api.event.interaction.SlashCommandCreateEvent;
import org.javacord.api.interaction.SlashCommandInteractionOption;
import org.javacord.api.interaction.callback.InteractionOriginalResponseUpdater;
import org.quartz.SchedulerException;
import org.springframework.stereotype.Component;

import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

@Invoker(
        alias = "delete-category-with-channel",
        description = "Removes a category with channels, except some Optional",
        type = CommandType.DEV,
        where = AllowedChannel.NEGO_SERVER,
        usage = "delete-category-with-channel (category to leave out)"
)
@Component
public class RemoveChannelsFromCategoryExceptOne implements Command {
    @Override
    public void execute(SlashCommandCreateEvent event) {
        CompletableFuture<InteractionOriginalResponseUpdater> respondLater = event.getSlashCommandInteraction().respondLater();
        List<Long> exceptionChannels = new ArrayList<>();
        List<SlashCommandInteractionOption> arguments = event.getSlashCommandInteraction().getArguments();
        if (arguments.size() > 0) {
            Optional<String> stringValue = arguments.get(0).getStringValue();
            stringValue.ifPresent(s -> exceptionChannels.addAll(
                    Arrays.stream(s.split("//s+")).map(Long::valueOf).toList()
            ));
        }
        EmbedBuilder em = new EmbedBuilder();
        em.setTitle("You are about to delete some category");
        String desMessage = "You are about to delete all the category, this action is irreversible";

        if (exceptionChannels.size() == 0) {
            desMessage = desMessage + "\nYou have chosen all the categories to be removed!";
        } else {
            String ids = exceptionChannels.stream().map(String::valueOf).toList().toString();
            desMessage = desMessage + "\nFollowing categories will not be deleted!\n" + ids;
        }
        em.setDescription(desMessage);
        em.setColor(Color.red);
        em.setTimestampToNow();

        Message join = respondLater.join()
                .addEmbed(em)
                .addComponents(
                        ActionRow.of(
                                List.of(
                                        Button.create("confirm", ButtonStyle.SUCCESS, "Do it bitch!"),
                                        Button.create("deny", ButtonStyle.DANGER, "Nah")
                                )
                        )
                )
                .update()
                .join();
        try {
            new ButtonRemoveJobScheduler().execute(join);
        } catch (SchedulerException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
        join.addButtonClickListener(btn -> {
            Server server = event.getSlashCommandInteraction().getServer().get();

            if (btn.getButtonInteraction().getCustomId().equals("deny")) {
                event.getSlashCommandInteraction().createFollowupMessageBuilder()
                        .setContent("Sure i'll abort!")
                        .send();
            } else if (btn.getButtonInteraction().getCustomId().equals("confirm")) {
                extracted(exceptionChannels, server);
                event.getSlashCommandInteraction().createFollowupMessageBuilder()
                        .setContent("Done")
                        .send();
            }
            btn.getButtonInteraction().getMessage().createUpdater().removeAllComponents().applyChanges();
        });
    }

    private static void extracted(List<Long> exceptionChannels, Server server) {
        List<ChannelCategory> channelCategories = server.getChannelCategories();
        for (ChannelCategory channelCategory : channelCategories) {
            boolean b = exceptionChannels.stream().anyMatch(x -> x == channelCategory.getId());
            if (b) continue;
            List<RegularServerChannel> channels = channelCategory.getChannels();
            for (RegularServerChannel channel : channels) {
                channel.delete();
            }
            channelCategory.delete();
        }
    }
}
