package com.spms.backend.jobs;

import com.spms.backend.jobs.data.DataPointCollector;
import com.spms.backend.service.sys.SystemStatisticsService;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Date;
import java.util.List;



public class DataProcessingJob extends BaseJob {

    //Saved to this service
    @Autowired
    SystemStatisticsService systemStatisticsService;

    @Autowired
    List<DataPointCollector> collectors;

    @Override
    protected void executeJob(JobExecutionContext context) throws JobExecutionException {
        // Get job parameters from context
        String jobName = context.getJobDetail().getKey().getName();
        String jobGroup = context.getJobDetail().getKey().getGroup();
        
        logger.info("Executing data processing job: {}/{}", jobGroup, jobName);
        
        try {
            // Collect statistics from various services
            collectors.forEach(x->{
                systemStatisticsService.recordStatistic(x.name(),x.description(),new Date(),x.getValueCurrentValue());
            });
            logger.info("Successfully saved system statistics");
        } catch (Exception e) {
            logger.error("Error processing system statistics", e);
            throw new JobExecutionException("Failed to process system statistics", e);
        }
    }
}
