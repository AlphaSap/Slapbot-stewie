package com.fthlbot.discordbotfthl.Util.Pagination;

import org.javacord.api.entity.message.Message;
import org.javacord.api.interaction.callback.InteractionOriginalResponseUpdater;
import org.quartz.*;
import org.quartz.impl.StdSchedulerFactory;

import java.util.Date;

public class PaginationJobScheduler {

    private static final long ONE_MINUTE_IN_MILLISECONDS = 60000;
    public void execute(InteractionOriginalResponseUpdater message) throws SchedulerException {
        JobDataMap dataMap = new JobDataMap();
        dataMap.put("message", message);

        JobDetail job = JobBuilder.newJob(PaginationJobs.class)
                .setJobData(dataMap)
                .build();
        Trigger trigger = TriggerBuilder.newTrigger()
                .startAt(new Date(System.currentTimeMillis() + ONE_MINUTE_IN_MILLISECONDS)).build();

        Scheduler s = StdSchedulerFactory.getDefaultScheduler();

        s.start();
        s.scheduleJob(job, trigger);
    }
}
