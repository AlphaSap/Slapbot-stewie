package com.fthlbot.discordbotfthl.Commands.CommandImpl.StaffCommandsImpl;

import com.fthlbot.discordbotfthl.Annotation.CommandType;
import com.fthlbot.discordbotfthl.Annotation.Invoker;
import com.fthlbot.discordbotfthl.DatabaseModels.Division.DivisionService;
import com.fthlbot.discordbotfthl.Handlers.Command;
import org.javacord.api.entity.message.component.ActionRow;
import org.javacord.api.entity.message.component.Button;
import org.javacord.api.entity.message.embed.EmbedBuilder;
import org.javacord.api.event.interaction.SlashCommandCreateEvent;
import org.springframework.stereotype.Component;

import java.awt.*;

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
        if (!event.getSlashCommandInteraction().getUser().isBotOwner()) {
            event.getSlashCommandInteraction().createImmediateResponder().setContent("You are not the bot owner").respond();
            return;
        }
        EmbedBuilder embedBuilder = new EmbedBuilder()
                .setTitle("Warning")
                .setDescription("This will delete all divisions and create new ones. Are you sure you want to do this?")
                .setColor(Color.ORANGE)
                .setTimestampToNow();
        event.getSlashCommandInteraction().createImmediateResponder().addEmbed(embedBuilder).addComponents(ActionRow.of(
                Button.primary("Yes", "yes"),
                Button.danger("No", "no")
        )).respond().thenAccept(message -> {
            message.update().join().addButtonClickListener(b -> {
                if (b.getButtonInteraction().getUser().getId() != event.getSlashCommandInteraction().getUser().getId()) {
                    return;
                }
                if (b.getButtonInteraction().getMessage().getContent().equals("yes")) {
                    divisionService.createDivisions();
                    event.getSlashCommandInteraction().createFollowupMessageBuilder()
                            .setContent("All divisions have been created")
                            .send();
                } else {
                    event.getSlashCommandInteraction().createFollowupMessageBuilder().setContent("Aborted").send();
                }
            });
        });
    }
}
