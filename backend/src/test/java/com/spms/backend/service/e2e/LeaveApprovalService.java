package com.spms.backend.service.e2e;

import org.flowable.common.engine.api.async.AsyncTaskInvoker;
import org.flowable.engine.delegate.DelegateExecution;
import org.flowable.engine.delegate.FutureJavaDelegate;

import java.util.concurrent.CompletableFuture;

public class LeaveApprovalService implements FutureJavaDelegate<String> {
    @Override
    public CompletableFuture<String> execute(DelegateExecution execution, AsyncTaskInvoker taskInvoker) {

        return CompletableFuture.completedFuture("Send Notification");
    }

    @Override
    public void afterExecution(DelegateExecution execution, String executionData) {
        System.out.println("Completed send notification");
    }

}
