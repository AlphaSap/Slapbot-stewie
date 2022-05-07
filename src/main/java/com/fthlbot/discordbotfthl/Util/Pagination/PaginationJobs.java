package com.fthlbot.discordbotfthl.Util.Pagination;

import org.javacord.api.entity.message.Message;
import org.javacord.api.interaction.callback.InteractionOriginalResponseUpdater;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class PaginationJobs implements Job {

    private InteractionOriginalResponseUpdater message;
    private final Logger log = LoggerFactory.getLogger(PaginationJobs.class);
    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
        Object message1 = context.getMergedJobDataMap().get("message");
        if (!(message1 instanceof InteractionOriginalResponseUpdater)) {
            throw new IllegalArgumentException("Message is not of type InteractionOriginalResponseUpdater");
        }
        message = (InteractionOriginalResponseUpdater) message1;
        log.info("Pagination job started");
        message.removeAllComponents().update();
    }
}
