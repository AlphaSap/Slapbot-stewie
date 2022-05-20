package com.fthlbot.discordbotfthl.Util.Pagination;

import org.javacord.api.entity.message.Message;
import org.javacord.api.interaction.callback.InteractionOriginalResponseUpdater;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class ButtonRemoveJobs implements Job {

    private final Logger log = LoggerFactory.getLogger(ButtonRemoveJobs.class);
    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
        Object message = context.getMergedJobDataMap().get("message");
        if (message instanceof InteractionOriginalResponseUpdater message1) {
            log.info("Pagination job started");
            message1.removeAllComponents().update();
        }else if (message instanceof Message message2) {
            log.info("Pagination job started");
            message2.createUpdater().removeAllComponents().applyChanges();
        }else {
            throw new IllegalArgumentException("Message is not of type InteractionOriginalResponseUpdater");
        }

    }
}
