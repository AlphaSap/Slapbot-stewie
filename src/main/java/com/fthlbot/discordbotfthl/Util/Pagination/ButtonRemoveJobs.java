package com.fthlbot.discordbotfthl.Util.Pagination;

import org.javacord.api.entity.message.Message;
import org.javacord.api.entity.message.component.*;
import org.javacord.api.interaction.callback.InteractionOriginalResponseUpdater;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class ButtonRemoveJobs implements Job {

    private final Logger log = LoggerFactory.getLogger(ButtonRemoveJobs.class);

    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
        Object message = context.getMergedJobDataMap().get("message");
        if (message instanceof InteractionOriginalResponseUpdater message1) {
            //TODO come back to it and fix it, it's not working. Not able to get buttons from the message object
            log.info("Pagination job started");
            message1.removeAllComponents().update();
        }else if (message instanceof Message message2) {
            log.info("Pagination job started");
            List<ActionRow> actionRows = message2
                    .getComponents()
                    .stream()
                    .filter(HighLevelComponent::isActionRow)
                    .map(x -> x.asActionRow().get())
                    .toList();

            List<LowLevelComponent> toAdd = getButtons(actionRows);
            message2.createUpdater().removeAllComponents()
                    .addComponents(ActionRow.of(toAdd))
                    .applyChanges();
        } else {
            throw new IllegalArgumentException("Message is not of type InteractionOriginalResponseUpdater");
        }

    }

    private List<LowLevelComponent> getButtons(List<ActionRow> actionRows) {
        List<LowLevelComponent> toAdd = new ArrayList<>();
        for (ActionRow actionRow : actionRows) {
            for (LowLevelComponent component : actionRow.getComponents()) {
                if (component instanceof Button button) {
                    ButtonBuilder buttonBuilder = new ButtonBuilder()
                            .copy(button)
                            .setDisabled(true);
                    toAdd.add(buttonBuilder.build());
                }
            }
        }
        return toAdd;
    }
}
