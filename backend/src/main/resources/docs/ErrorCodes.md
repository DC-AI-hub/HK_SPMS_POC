# Error Codes Documentation

## Validation Errors (400)
- `VALIDATION_FAILED`: Input validation failed
- `INVALID_ID_FORMAT`: Invalid ID format provided
- `MISSING_REQUIRED_FIELD`: Required field is missing

## Not Found Errors (404)
- `PROCESS_NOT_FOUND`: Process definition not found
- `VERSION_NOT_FOUND`: Process version not found
- `INSTANCE_NOT_FOUND`: Process instance not found

## Internal Server Errors (500)
- `PROCESS_START_FAILED`: Failed to start process instance
- `TASK_COMPLETION_FAILED`: Failed to complete task
- `DEPLOYMENT_FAILED`: Process deployment failed

## Usage Examples
```java
// Validation example
throw new ValidationException("INVALID_ID_FORMAT", "Invalid process ID format");

// Not found example  
throw new NotFoundException("PROCESS_NOT_FOUND", "Process with ID 123 not found");

// Runtime example
throw new SpmsRuntimeException("DEPLOYMENT_FAILED", "Failed to deploy process");
