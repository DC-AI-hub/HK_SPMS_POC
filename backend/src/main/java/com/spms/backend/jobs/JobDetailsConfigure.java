package com.spms.backend.jobs;

import org.quartz.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Date;

@Configuration
public class JobDetailsConfigure {


    

    @Bean
    public JobDetail dataProcessJobDetail() {
        return JobBuilder.newJob(DataProcessingJob.class)
                .withIdentity(DataProcessingJob.class.getSimpleName())
                .storeDurably()
                .build();
    }
    @Bean
    public Trigger dataProcessJobTrigger() {
        SimpleScheduleBuilder schedule = SimpleScheduleBuilder.simpleSchedule()
                .withIntervalInSeconds(30)
                .repeatForever();

        return TriggerBuilder.newTrigger()
                .forJob(dataProcessJobDetail())
                .startAt(new Date())
                .withIdentity(DataProcessingJob.class.getSimpleName())
                .withSchedule(schedule)
                .build();
    }

}
