# Job Implementation Practice Guide

## 1. Creating New Jobs
### Extending BaseJob Pattern
```java
public class SampleJob extends BaseJob {
    @Override
    protected void executeJob(JobExecutionContext context) {
        // Job-specific implementation
    }
}
```
- Always extend BaseJob for consistent logging
- Keep executeJob() focused on business logic

### Parameter Validation Example (CleanupJob)
```java
protected void executeJob(JobExecutionContext context) {
    int retentionDays = context.getMergedJobDataMap().getIntValue("retentionDays");
    if (retentionDays <= 0) {
        throw new IllegalArgumentException("retentionDays must be positive");
    }
    // Proceed with cleanup
}
```

## 2. Service Integration Best Practices
### Dependency Injection Pattern
```java
public class DataProcessingJob extends BaseJob {
    @Autowired
    private SystemStatisticsService statisticsService;
    
    @Autowired
    private List<DataPointCollector> collectors;
    
    protected void executeJob(JobExecutionContext context) {
        collectors.forEach(collector -> {
            statisticsService.recordStatistic(
                collector.name(),
                collector.description(),
                new Date(),
                collector.getValue()
            );
        });
    }
}
```
- Use constructor injection when possible
- Prefer interfaces over concrete implementations
- Keep service interactions transactional

## 3. Implementing TODO Operations
### CleanupJob Implementation
```java
protected void executeJob(JobExecutionContext context) {
    boolean dryRun = context.getMergedJobDataMap().getBoolean("dryRun");
    int retentionDays = context.getMergedJobDataMap().getIntValue("retentionDays");
    
    if (!dryRun) {
        // 1. Log file rotation
        logService.rotateLogs(retentionDays);
        
        // 2. Temporary file cleanup
        tempFileService.deleteOldTempFiles(retentionDays);
        
        // 3. Database records cleanup
        recordCleanupService.archiveOldRecords(retentionDays);
        
        // 4. Cache clearing
        cacheService.clearExpiredEntries();
    }
}
```

### ReportGenerationJob Implementation
```java
protected void executeJob(JobExecutionContext context) {
    String reportType = context.getMergedJobDataMap().getString("reportType");
    
    // 1. Fetch data from services
    ReportData data = reportDataService.fetchReportData(reportType);
    
    // 2. Apply template
    byte[] report = templateService.applyTemplate(reportType, data);
    
    // 3. Generate output
    String outputPath = reportStorageService.storeReport(report, reportType);
    
    // 4. Distribute report
    notificationService.notifyRecipients(outputPath, reportType);
}
```

## 4. Testing Strategies
### Unit Testing Job Logic
```java
@Test
public void testCleanupJobDryRun() {
    CleanupJob job = new CleanupJob();
    JobExecutionContext context = mock(JobExecutionContext.class);
    JobDataMap dataMap = new JobDataMap();
    dataMap.put("dryRun", true);
    dataMap.put("retentionDays", 30);
    
    when(context.getMergedJobDataMap()).thenReturn(dataMap);
    
    job.executeJob(context);
    
    // Verify no destructive operations occurred
}
```

### Integration Testing
```java
@SpringBootTest
public class ReportGenerationJobIntegrationTest {
    @Autowired
    private ReportGenerationJob job;
    
    @Test
    public void testReportGeneration() {
        JobExecutionContext context = createTestContext("reportType", "daily");
        job.executeJob(context);
        // Verify report generation and storage
    }
}
```

## 5. Best Practices Summary
1. Keep jobs focused on workflow coordination
2. Delegate business logic to services
3. Validate all job parameters
4. Implement comprehensive error handling
5. Include metrics collection for job execution
6. Document job parameters and expected behavior
