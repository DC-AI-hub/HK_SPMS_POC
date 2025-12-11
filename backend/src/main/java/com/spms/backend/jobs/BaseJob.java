package com.spms.backend.jobs;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class BaseJob implements Job {

    protected final Logger logger = LoggerFactory.getLogger(getClass());

    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
        try {
            logger.info("Starting job execution: {}", getClass().getSimpleName());
            executeJob(context);
            logger.info("Completed job execution: {}", getClass().getSimpleName());
        } catch (Exception e) {
            logger.error("Job execution failed", e);
            throw new JobExecutionException(e);
        }
    }

    protected abstract void executeJob(JobExecutionContext context) throws JobExecutionException;
}
