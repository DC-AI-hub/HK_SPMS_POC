package com.spms.backend.service.process;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

/**
 * 统一的表单数据处理器
 * 用于标准化不同类型表单的数据结构和处理逻辑
 */
@Slf4j
@Component
public class FormDataProcessor {

    private final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * 表单数据类型枚举
     */
    public enum FormType {
        LEAVE,
        OTHER
    }

    /**
     * 统一表单数据结构
     */
    public static class UnifiedFormData {
        private FormType formType;
        private Map<String, Object> formData;
        private String processInstanceId;

        public UnifiedFormData(FormType formType, Map<String, Object> formData) {
            this.formType = formType;
            this.formData = formData;
        }

        // Getters and setters
        public FormType getFormType() { return formType; }
        public void setFormType(FormType formType) { this.formType = formType; }
        public Map<String, Object> getFormData() { return formData; }
        public void setFormData(Map<String, Object> formData) { this.formData = formData; }
        public String getProcessInstanceId() { return processInstanceId; }
        public void setProcessInstanceId(String processInstanceId) { this.processInstanceId = processInstanceId; }
    }


    /**
     * 处理请假表单数据
     */
    public UnifiedFormData processLeaveData(String employeeName, String leaveType,
                                          String startDate, String endDate, String reason) {
        Map<String, Object> formData = new HashMap<>();
        formData.put("employeeName", employeeName);
        formData.put("leaveType", leaveType);
        formData.put("startDate", startDate);
        formData.put("endDate", endDate);
        formData.put("reason", reason);

        return new UnifiedFormData(FormType.LEAVE, formData);
    }

    /**
     * 序列化为字符串格式（供Flowable使用）
     */
    public String serializeUnifiedFormData(UnifiedFormData unifiedFormData) {
        try {
            Map<String, Object> serializedData = new HashMap<>();
            serializedData.put("formType", unifiedFormData.getFormType().name());
            serializedData.put("formData", unifiedFormData.getFormData());
            return objectMapper.writeValueAsString(serializedData);
        } catch (Exception e) {
            log.error("Failed to serialize unified form data", e);
            throw new RuntimeException("Form data serialization failed", e);
        }
    }

    /**
     * 从字符串反序列化
     */
    public UnifiedFormData deserializeUnifiedFormData(String serializedData) {
        try {
            JsonNode jsonNode = objectMapper.readTree(serializedData);
            String formTypeStr = jsonNode.get("formType").asText();
            FormType formType = FormType.valueOf(formTypeStr);

            JsonNode formDataNode = jsonNode.get("formData");
            Map<String, Object> formData = objectMapper.convertValue(
                formDataNode,
                new com.fasterxml.jackson.core.type.TypeReference<Map<String, Object>>() {}
            );

            return new UnifiedFormData(formType, formData);
        } catch (Exception e) {
            log.error("Failed to deserialize unified form data", e);
            throw new RuntimeException("Form data deserialization failed", e);
        }
    }

    /**
     * 获取表单的展示信息
     */
    public Map<String, String> getDisplayInfo(UnifiedFormData unifiedFormData) {
        Map<String, String> displayInfo = new HashMap<>();

        switch (unifiedFormData.getFormType()) {
            case LEAVE:
                displayInfo.put("title", "请假申请审批");
                displayInfo.put("requester", (String) unifiedFormData.getFormData().get("employeeName"));
                displayInfo.put("leaveType", (String) unifiedFormData.getFormData().get("leaveType"));
                displayInfo.put("startDate", (String) unifiedFormData.getFormData().get("startDate"));
                displayInfo.put("endDate", (String) unifiedFormData.getFormData().get("endDate"));
                break;

            default:
                displayInfo.put("title", "审批请求");
                displayInfo.put("requester", "未知");
                break;
        }

        return displayInfo;
    }

    private String extractJsonValue(JsonNode node, String path) {
        try {
            JsonNode valueNode = node.at(path);
            return valueNode.isMissingNode() || valueNode.isNull() ? "未知" : valueNode.asText();
        } catch (Exception e) {
            return "未知";
        }
    }
}
