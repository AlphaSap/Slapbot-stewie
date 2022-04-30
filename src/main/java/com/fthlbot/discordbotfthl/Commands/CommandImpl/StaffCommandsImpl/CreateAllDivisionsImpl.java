package com.fthlbot.discordbotfthl.Commands.CommandImpl.StaffCommandsImpl;

import com.fthlbot.discordbotfthl.Annotation.CommandType;
import com.fthlbot.discordbotfthl.Annotation.Invoker;
import com.fthlbot.discordbotfthl.DatabaseModels.Division.DivisionService;
import com.fthlbot.discordbotfthl.Handlers.Command;
import org.javacord.api.event.interaction.SlashCommandCreateEvent;
import org.springframework.stereotype.Component;

@Component
@Invoker(
        alias = "create-all-divisions",
        description = "Creates all divisions",
        usage = "create-all-divisions",
        type = CommandType.STAFF
)
public class CreateAllDivisionsImpl implements Command {

    private final DivisionService divisionService;

    public CreateAllDivisionsImpl(DivisionService divisionService) {
        this.divisionService = divisionService;
    }

    @Override
    public void execute(SlashCommandCreateEvent event) {
        if(event.getSlashCommandInteraction().getUser().isBotOwner()){
            divisionService.createDivisions();
            event.getSlashCommandInteraction().createImmediateResponder().setContent("Created all divisions").respond();
            return;
        }
        event.getSlashCommandInteraction().createImmediateResponder().setContent("You are not the bot owner").respond();
    }
}
