package com.fthlbot.discordbotfthl.Commands.CommandImpl.StaffCommandsImpl;

import com.fthlbot.discordbotfthl.core.Annotation.CommandType;
import com.fthlbot.discordbotfthl.core.Annotation.Invoker;
import com.fthlbot.discordbotfthl.DatabaseModels.Division.Division;
import com.fthlbot.discordbotfthl.DatabaseModels.Division.DivisionService;
import com.fthlbot.discordbotfthl.DatabaseModels.Exception.EntityNotFoundException;
import com.fthlbot.discordbotfthl.core.Handlers.Command;
import com.fthlbot.discordbotfthl.Util.GeneralService;
import org.javacord.api.entity.message.MessageFlag;
import org.javacord.api.entity.message.component.ActionRow;
import org.javacord.api.entity.message.component.TextInput;
import org.javacord.api.entity.message.component.TextInputStyle;
import org.javacord.api.entity.message.embed.EmbedBuilder;
import org.javacord.api.event.interaction.SlashCommandCreateEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


@Invoker(
        alias = "division-editor",
        description = "Edit a division",
        usage = "/division-editor (type in the modal form)",
        type = CommandType.DEV
)
@Component
public class DivisionEditorImpl implements Command {
    private final DivisionService divisionService;

    public DivisionEditorImpl(DivisionService divisionService) {
        this.divisionService = divisionService;
    }

    private final Logger logger = LoggerFactory.getLogger(DivisionEditorImpl.class);
    @Override
    public void execute(SlashCommandCreateEvent event) {

        logger.info("DivisionEditorImpl.execute()");
        if (!event.getSlashCommandInteraction().getUser().isBotOwner()) {
            event.getSlashCommandInteraction().createImmediateResponder().setFlags(MessageFlag.EPHEMERAL)
                    .setContent("This command is restricted to bot owner")
                    .respond();
            return;
        }


         /*
          * A Slashcommand
          * args - [division name]
          * args - value they want to change?
          * modal form to enter new values?
          */
        String divisionName = event.getSlashCommandInteraction().getArguments().get(0).getStringValue().get();
        String fieldName = event.getSlashCommandInteraction().getArguments().get(1).getStringValue().get();

        event.getSlashCommandInteraction().respondWithModal(
                "e",
                "Edit Division",
                ActionRow.of(
                        TextInput.create(
                                TextInputStyle.SHORT,
                                "new value",
                                "Enter the new value for " + fieldName,
                                true
                        )
                )
        ).join();
        event.getApi().addModalSubmitListener(mse -> {
            String customId = mse.getModalInteraction().getCustomId();
            if (!customId.equals("e")){
                return;
            }
            if (mse.getModalInteraction().getUser().getId() != event.getSlashCommandInteraction().getUser().getId()){
                return;
            }
            Division division = null;
            try {
                division = divisionService.getDivisionByAlias(divisionName);
            } catch (EntityNotFoundException e) {
                GeneralService.getLeagueError(e, event);
                return;
            }
            String new_value = mse.getModalInteraction().getTextInputValueByCustomId("new value").get();
            EmbedBuilder eb = new EmbedBuilder()
                    .setTitle("Division Editor")
                    .setColor(Color.CYAN)
                    .setAuthor(event.getSlashCommandInteraction().getUser())
                    .setTimestampToNow();

            switch (fieldName){
                case "name" -> {
                    division.setName(new_value);
                    eb.addField("Name", "Changed to " + new_value, true);
                }
                case "alias" -> {
                    division.setAlias(new_value);
                    eb.addField("Alias", "Changed to " + new_value, true);
                }
                case "allowed roster change" -> {
                    int i = 0;
                    try {
                        i = Integer.parseInt(new_value);
                        division.setAllowedRosterChanges(i);
                        eb.addField("Allowed roster changes", "Changed to " + i, true);
                    } catch (NumberFormatException e) {
                        eb.setColor(Color.RED);
                        eb.addField("Error", "Invalid value for allowed roster change", true);
                    }
                }
                case "roster size" -> {
                    int i = 0;
                    try {
                        i = Integer.parseInt(new_value);
                        division.setRosterSize(i);
                        eb.addField("Roster size", "Changed to " + i, true);
                    } catch (NumberFormatException e) {
                        eb.setColor(Color.RED);
                        eb.addField("Error", "Invalid value for roster size", true);
                    }
                }
                case "allowed townhall" -> {
                    List<Integer> allowedTownhalls = new ArrayList<>();
                    String[] split = new_value.split(",|\\s+");
                    for (String s : split) {
                        try {
                            int i = Integer.parseInt(s);
                            allowedTownhalls.add(i);
                        } catch (NumberFormatException e) {
                            eb.setColor(Color.RED);
                            eb.addField("Error", "Invalid value for allowed townhall", true);
                            break;
                        }
                    }
                    Integer[] allowedTownHall = allowedTownhalls.toArray(new Integer[0]);
                    division.setAllowedTownHall(allowedTownHall);
                    eb.addField("Allowed townhall", "Changed to " + Arrays.toString(allowedTownHall) , true);
                }
            }
        });

    }
}
