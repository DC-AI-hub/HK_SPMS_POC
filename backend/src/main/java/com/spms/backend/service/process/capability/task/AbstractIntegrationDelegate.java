package com.spms.backend.service.process.capability.task;

import com.spms.backend.service.IntegrationConfigService;
import org.flowable.engine.delegate.DelegateExecution;
import org.flowable.engine.delegate.JavaDelegate;
import org.springframework.retry.RetryCallback;
import org.springframework.retry.RetryContext;
import org.springframework.retry.support.RetryTemplate;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public abstract class AbstractIntegrationDelegate implements JavaDelegate {

    protected final IntegrationConfigService configService;
    protected final RetryTemplate retryTemplate;

    public AbstractIntegrationDelegate(
        IntegrationConfigService configService,
        RetryTemplate retryTemplate
    
    ) {
        this.configService=configService;
        this.retryTemplate = retryTemplate;
    }

    @Override
    public void execute(DelegateExecution execution) {
        try {
            
            retryTemplate.execute(new RetryCallback<Void, Exception>() {
                @Override
                public Void doWithRetry(RetryContext context) throws Exception {
                    doExecute(execution);
                    return null;
                }
            });
        } catch (Exception e) {
            //log.error("Integration task failed after {} retries", config.getRetryCount(), e);
            handleFailure(execution, e);
        }
    }

    protected abstract void doExecute(DelegateExecution execution);

    protected void handleFailure(DelegateExecution execution, Exception e) {
        execution.setVariable("integrationError", e.getMessage());
    }
}
