package com.spms.backend.model;

/**
 * 项目状态枚举
 * 定义项目在生命周期中的各种状态
 */
public enum ProjectStatus {
    /**
     * 活跃状态 - 项目正在进行中
     */
    ACTIVE,
    
    /**
     * 非活跃状态 - 项目暂停或冻结
     */
    INACTIVE,
    
    /**
     * 已完成状态 - 项目已结束
     */
    COMPLETED
}
