package com.spms.backend.service.process;

import com.spms.backend.service.process.impl.ProcessDataServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.flowable.common.engine.api.async.AsyncTaskInvoker;
import org.flowable.engine.delegate.DelegateExecution;
import org.flowable.engine.delegate.FutureJavaDelegate;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.concurrent.CompletableFuture;

/**
 * 通用审批服务抽象基类
 *
 * 提供统一的审批处理框架，子类只需要实现业务特定的逻辑
 */
@Slf4j
public abstract class AbstractApprovalService implements FutureJavaDelegate<String> {

    @Autowired
    protected ProcessDataServiceImpl processDataService;

    @Autowired
    protected FormDataProcessor formDataProcessor;

    @Override
    public CompletableFuture<String> execute(DelegateExecution execution, AsyncTaskInvoker taskInvoker) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                // 获取通用参数
                String processInstanceId = execution.getProcessInstanceId();
                String approvalDecision = (String) execution.getVariable("approvalDecision");
                String approvalComment = (String) execution.getVariable("approvalComment");

                log.info("Processing approval for process: {}, decision: {}",
                        processInstanceId, approvalDecision);

                // 调用子类的业务逻辑处理
                ApprovalResult result = processApproval(processInstanceId, approvalDecision, approvalComment);

                // 设置流程变量
                execution.setVariable("statusUpdateCompleted", true);
                execution.setVariable("finalStatus", result.getStatus());
                execution.setVariable("approvalResult", result.getMessage());

                String responseMessage = String.format("Approval processed: %s -> %s",
                    processInstanceId, result.getStatus());
                log.info(responseMessage);

                return responseMessage;

            } catch (Exception e) {
                log.error("Failed to process approval", e);

                // 设置错误状态
                execution.setVariable("statusUpdateCompleted", false);
                execution.setVariable("statusUpdateError", e.getMessage());

                throw new RuntimeException("Approval processing failed: " + e.getMessage(), e);
            }
        });
    }

    @Override
    public void afterExecution(DelegateExecution execution, String executionData) {
        String processInstanceId = execution.getProcessInstanceId();
        String finalStatus = (String) execution.getVariable("finalStatus");

        log.info("Approval service completed: processInstanceId={}, finalStatus={}, result={}",
                processInstanceId, finalStatus, executionData);

        // 调用子类的后处理逻辑
        afterApproval(processInstanceId, finalStatus, executionData);
    }

    /**
     * 审批处理结果
     */
    public static class ApprovalResult {
        private final String status;
        private final String message;

        public ApprovalResult(String status, String message) {
            this.status = status;
            this.message = message;
        }

        public String getStatus() { return status; }
        public String getMessage() { return message; }
    }

    /**
     * 子类实现具体的审批业务逻辑
     *
     * @param processInstanceId 流程实例ID
     * @param approvalDecision 审批决定（approve/reject）
     * @param approvalComment 审批意见
     * @return 审批结果
     */
    protected abstract ApprovalResult processApproval(String processInstanceId,
                                                    String approvalDecision,
                                                    String approvalComment);

    /**
     * 子类实现审批后的处理逻辑（可选）
     *
     * @param processInstanceId 流程实例ID
     * @param finalStatus 最终状态
     * @param executionData 执行数据
     */
    protected void afterApproval(String processInstanceId, String finalStatus, String executionData) {
        // 默认空实现，子类可重写
    }

    /**
     * 获取BPMN字段值的辅助方法
     */
    protected String getFieldValue(DelegateExecution execution, String fieldName) {
        try {
            Object fieldValue = execution.getVariable(fieldName);
            return fieldValue != null ? fieldValue.toString() : null;
        } catch (Exception e) {
            log.warn("Failed to get field value: {}", fieldName, e);
            return null;
        }
    }
}
