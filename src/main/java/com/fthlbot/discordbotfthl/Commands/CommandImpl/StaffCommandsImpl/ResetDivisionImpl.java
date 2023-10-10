package com.fthlbot.discordbotfthl.Commands.CommandImpl.StaffCommandsImpl;

import com.fthlbot.discordbotfthl.Commands.CommandListener.ResetDivision;
import com.fthlbot.discordbotfthl.DatabaseModels.Division.DivisionService;
import com.fthlbot.discordbotfthl.core.Annotation.CommandType;
import com.fthlbot.discordbotfthl.core.Annotation.Invoker;
import org.javacord.api.event.interaction.SlashCommandCreateEvent;
import org.springframework.stereotype.Component;

@Component
@Invoker(
        alias = "reset-division",
        description = "Resets the division",
        usage = "reset-division",
        type = CommandType.DEV
)
public class ResetDivisionImpl implements ResetDivision {

    private final DivisionService divisionService;

    public ResetDivisionImpl(DivisionService divisionService) {
        this.divisionService = divisionService;
    }

    @Override
    public void execute(SlashCommandCreateEvent event) {
        divisionService.correctTheDivision();
        event.getSlashCommandInteraction().createImmediateResponder()
                .setContent("Divisions have been reset")
                .respond();
    }
}
