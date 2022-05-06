package com.fthlbot.discordbotfthl.Util.Pagination;

import org.javacord.api.interaction.callback.InteractionOriginalResponseUpdater;
import org.quartz.*;
import org.quartz.impl.StdSchedulerFactory;

import java.util.Date;

public class PaginationJobExecutor {

    protected void execute(InteractionOriginalResponseUpdater message) throws SchedulerException {
        JobDataMap dataMap = new JobDataMap();
        dataMap.put("message", message);

        JobDetail job = JobBuilder.newJob(PaginationJobs.class)
                .setJobData(dataMap)
                .build();
        Trigger trigger = TriggerBuilder.newTrigger().withIdentity("SayHello")
                .startAt(new Date(System.currentTimeMillis() + 5000)).build();

        Scheduler s = StdSchedulerFactory.getDefaultScheduler();

        s.start();
        s.scheduleJob(job, trigger);
    }
}
