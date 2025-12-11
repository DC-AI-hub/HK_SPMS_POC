package com.spms.backend.controller.sys;

import org.quartz.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.spms.backend.controller.dto.process.JobScheduleRequest;
import com.spms.backend.controller.dto.process.JobStatusResponse;
import com.spms.backend.jobs.BaseJob;
import com.spms.backend.jobs.DataProcessingJob;
import com.spms.backend.jobs.ReportGenerationJob;
import com.spms.backend.jobs.CleanupJob;

@RestController
@RequestMapping("/api/v1/jobs")
public class JobControllerV1 {

    private final Scheduler scheduler;

    @Autowired
    public JobControllerV1(Scheduler scheduler) {
        this.scheduler = scheduler;
    }

    @PostMapping("/schedule")
    public ResponseEntity<?> scheduleJob(@RequestBody JobScheduleRequest request) {
        try {
            // Validate request
            if (request.getJobName() == null || request.getJobName().isEmpty()) {
                return ResponseEntity.badRequest().body("Job name is required");
            }
            if (request.getCronExpression() == null || request.getCronExpression().isEmpty()) {
                return ResponseEntity.badRequest().body("Cron expression is required");
            }

            // Determine job class based on type
            Class<? extends BaseJob> jobClass = switch (request.getJobType()) {
                case "data" -> DataProcessingJob.class;
                case "report" -> ReportGenerationJob.class;
                case "cleanup" -> CleanupJob.class;
                default -> throw new IllegalArgumentException("Invalid job type: " + request.getJobType());
            };

            // Create job detail
            JobDetail jobDetail = JobBuilder.newJob(jobClass)
                    .withIdentity(request.getJobName(), "spms-jobs")
                    .usingJobData(new JobDataMap(request.getJobParameters()))
                    .storeDurably()
                    .build();

            // Create trigger
            Trigger trigger = TriggerBuilder.newTrigger()
                    .withIdentity(request.getJobName() + "-trigger", "spms-triggers")
                    .withSchedule(CronScheduleBuilder.cronSchedule(request.getCronExpression()))
                    .forJob(jobDetail)
                    .build();

            // Schedule the job
            scheduler.scheduleJob(jobDetail, trigger);
            return ResponseEntity.ok().body("Job scheduled successfully");
        } catch (SchedulerException | IllegalArgumentException e) {
            return ResponseEntity.internalServerError().body("Failed to schedule job: " + e.getMessage());
        }
    }

    @PostMapping("/trigger/{jobName}")
    public ResponseEntity<?> triggerJob(@PathVariable String jobName) {
        try {
            JobKey jobKey = new JobKey(jobName, "spms-jobs");
            if (!scheduler.checkExists(jobKey)) {
                return ResponseEntity.notFound().build();
            }
            scheduler.triggerJob(jobKey);
            return ResponseEntity.ok().body("Job triggered successfully");
        } catch (SchedulerException e) {
            return ResponseEntity.internalServerError().body("Failed to trigger job: " + e.getMessage());
        }
    }

    @GetMapping("/status/{jobName}")
    public ResponseEntity<?> getJobStatus(@PathVariable String jobName) {
        try {
            JobKey jobKey = new JobKey(jobName, "spms-jobs");
            if (!scheduler.checkExists(jobKey)) {
                return ResponseEntity.notFound().build();
            }

            JobDetail jobDetail = scheduler.getJobDetail(jobKey);
            Trigger trigger = scheduler.getTriggersOfJob(jobKey).get(0);

            JobStatusResponse response = new JobStatusResponse();
            response.setJobName(jobName);
            response.setJobClass(jobDetail.getJobClass().getSimpleName());
            response.setNextFireTime(trigger.getNextFireTime());
            response.setTriggerState(scheduler.getTriggerState(trigger.getKey()));
            
            return ResponseEntity.ok(response);
        } catch (SchedulerException e) {
            return ResponseEntity.internalServerError().body("Failed to get job status: " + e.getMessage());
        }
    }

    @GetMapping("/history/{jobName}")
    public ResponseEntity<?> getJobHistory(@PathVariable String jobName) {
        // TODO: Implement job execution history
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{jobName}")
    public ResponseEntity<?> deleteJob(@PathVariable String jobName) {
        try {
            JobKey jobKey = new JobKey(jobName, "spms-jobs");
            if (!scheduler.checkExists(jobKey)) {
                return ResponseEntity.notFound().build();
            }
            boolean deleted = scheduler.deleteJob(jobKey);
            return deleted ? 
                ResponseEntity.ok().body("Job deleted successfully") :
                ResponseEntity.internalServerError().body("Failed to delete job");
        } catch (SchedulerException e) {
            return ResponseEntity.internalServerError().body("Failed to delete job: " + e.getMessage());
        }
    }

    // TODO: Create JobScheduleRequest DTO
}
