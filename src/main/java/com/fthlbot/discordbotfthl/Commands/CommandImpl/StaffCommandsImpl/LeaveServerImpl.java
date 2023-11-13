package com.fthlbot.discordbotfthl.Commands.CommandImpl.StaffCommandsImpl;

import com.fthlbot.discordbotfthl.core.Annotation.CommandType;
import com.fthlbot.discordbotfthl.core.Annotation.Invoker;
import com.fthlbot.discordbotfthl.core.Handlers.Command;
import org.javacord.api.entity.server.Server;
import org.javacord.api.event.interaction.SlashCommandCreateEvent;
import org.javacord.api.interaction.callback.InteractionOriginalResponseUpdater;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

@Invoker(
        alias = "leave-server",
        description = "Leaves the server.",
        usage = "leave-server <server id> <message> <cruel>",
        type = CommandType.DEV
)
@Component
public class LeaveServerImpl implements Command {
    @Override
    public void execute(SlashCommandCreateEvent event) {

        CompletableFuture<InteractionOriginalResponseUpdater> response
                = event.getSlashCommandInteraction().respondLater();

        Optional<String> serverIDString = event.getSlashCommandInteraction().getArguments().get(0)
                .getStringValue();
        if (serverIDString.isEmpty()) {
            response.thenAccept(r -> r.setContent("Invalid server id.").update().join());
            return;
        }

        Optional<Long> serverId = serverIDString.map(Long::parseLong);
        if (serverId.isEmpty()) {
            response.thenAccept(r -> r.setContent("Invalid server id.").update().join());
            return;
        }

        Optional<Server> serverById = event.getSlashCommandInteraction().getApi().getServerById(serverId.get());
        if (serverById.isEmpty()){
            response.thenAccept(r -> r.setContent("Invalid server id.").update().join());
            return;
        }
        Optional<String> message = event.getSlashCommandInteraction().getArguments().get(1)
                .getStringValue();

        boolean beCruel = event.getSlashCommandInteraction().getArguments().get(2)
                .getBooleanValue()
                .orElse(false);

        if (message.isEmpty()) {
            response.thenAccept(r -> r.setContent("Invalid Message.").update().join());
            return;
        }

        if (beCruel) {
            // Message the owner of the server
            serverById.get().getOwner().get().sendMessage(message.get());

            // Write in all the channels that the bot is leaving
            serverById.get().getChannels().stream().filter(ch -> {
                ch.updateName("SUCK MY DICK").join();
                return ch.getType().isTextChannelType();
            }).filter(ch -> ch.asTextChannel().isPresent())
                    .forEach(ch -> ch.asTextChannel().get().sendMessage(message.get()).join());
            serverById.get().updateName("LMAOOOOOOOOOOOO PHISER").join();
        }
        serverById.ifPresent(s -> {
            try {
                s.leave().get();
            } catch (InterruptedException | ExecutionException e) {
                throw new RuntimeException(e);
            }
        });
        response.thenAccept(rr-> rr.setContent("Left").update()).join();
    }
}
