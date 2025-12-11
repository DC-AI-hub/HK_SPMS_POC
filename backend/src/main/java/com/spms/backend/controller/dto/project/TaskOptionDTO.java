package com.spms.backend.controller.dto.project;

import java.util.List;

/**
 * 任务选项数据传输对象
 * 用于下拉选择等场景，只包含基本信息以优化性能
 */
public record TaskOptionDTO(
    Long id,
    String taskNumber,
    String taskName,
    List<String> activities
) {
    /**
     * 紧凑构造器，用于数据验证
     */
    public TaskOptionDTO {
        if (id == null) {
            throw new IllegalArgumentException("任务ID不能为空");
        }
        if (taskNumber == null || taskNumber.isBlank()) {
            throw new IllegalArgumentException("任务编号不能为空");
        }
        if (taskName == null || taskName.isBlank()) {
            throw new IllegalArgumentException("任务名称不能为空");
        }
    }
}
