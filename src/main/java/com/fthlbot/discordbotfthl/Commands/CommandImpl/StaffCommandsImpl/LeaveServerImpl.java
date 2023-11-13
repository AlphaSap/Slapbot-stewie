package com.fthlbot.discordbotfthl.Commands.CommandImpl.StaffCommandsImpl;

import com.fthlbot.discordbotfthl.core.Annotation.CommandType;
import com.fthlbot.discordbotfthl.core.Annotation.Invoker;
import com.fthlbot.discordbotfthl.core.CommandCreation.Option;
import com.fthlbot.discordbotfthl.core.Handlers.Command;
import org.javacord.api.entity.server.Server;
import org.javacord.api.event.interaction.SlashCommandCreateEvent;

import java.util.Optional;

@Invoker(
        alias = "leave-server",
        description = "Leaves the server.",
        usage = "leave-server <server id>",
        type = CommandType.DEV
)
public class LeaveServerImpl implements Command {
    @Override
    public void execute(SlashCommandCreateEvent event) {

        Optional<Long> serverId = event.getSlashCommandInteraction().getArguments().get(0)
                .getLongValue();
        if (serverId.isEmpty()){
            event.getSlashCommandInteraction().createImmediateResponder().setContent("Invalid server id.").respond();
            return;
        }

        Optional<Server> serverById = event.getSlashCommandInteraction().getApi().getServerById(serverId.get());
        if (serverById.isEmpty()){
            event.getSlashCommandInteraction().createImmediateResponder().setContent("Invalid server id.").respond();
            return;
        }
        serverById.get().leave();
        event.getSlashCommandInteraction().createImmediateResponder().setContent("Left").respond();
    }
}
