package com.spms.backend.controller.dto.project;

import java.util.List;

/**
 * 项目选项数据传输对象
 * 用于下拉选择等场景，包含基础信息与嵌套任务选项
 */
public record ProjectOptionDTO(
    Long id,
    String projectCode,
    String projectName,
    List<TaskOptionDTO> tasks
) {
    /**
     * 紧凑构造器，用于数据验证
     */
    public ProjectOptionDTO {
        if (id == null) {
            throw new IllegalArgumentException("项目ID不能为空");
        }
        if (projectCode == null || projectCode.isBlank()) {
            throw new IllegalArgumentException("项目代码不能为空");
        }
        if (projectName == null || projectName.isBlank()) {
            throw new IllegalArgumentException("项目名称不能为空");
        }
    }
}
