package com.spms.backend.repository.entities.timecard;

import com.spms.backend.repository.entities.BaseEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDate;

/**
 * 节假日实体类
 * 映射到 spms_holiday 表
 */
@Entity
@Table(name = "spms_holiday")
@Data
@EqualsAndHashCode(callSuper = true)
public class HolidayEntity extends BaseEntity {
    
    /**
     * 节假日日期
     */
    @Column(name = "holiday_date", nullable = false)
    @NotNull(message = "节假日日期不能为空")
    private LocalDate holidayDate;
    
    /**
     * 国家代码（CN/HK/US/UK）
     */
    @Column(name = "country", nullable = false, length = 10)
    @NotNull(message = "国家代码不能为空")
    private String country;
    
    /**
     * 节假日名称
     */
    @Column(name = "name", nullable = false, length = 200)
    @NotNull(message = "节假日名称不能为空")
    private String name;
    
    /**
     * 节假日类型（PUBLIC_HOLIDAY/COMPANY_HOLIDAY）
     */
    @Column(name = "type", nullable = false, length = 50)
    @NotNull(message = "节假日类型不能为空")
    private String type;
    
    /**
     * 是否激活（用于软删除）
     */
    @Column(name = "is_active", nullable = false)
    private Boolean isActive = true;
    
    /**
     * 创建时间戳（毫秒）
     */
    @Column(name = "created_at", nullable = false)
    private Long createdAt;
    
    /**
     * 更新时间戳（毫秒）
     */
    @Column(name = "updated_at", nullable = false)
    private Long updatedAt;
    
    /**
     * 创建人 User ID
     */
    @Column(name = "created_by", nullable = false)
    private Long createdBy;
    
    /**
     * 更新人 User ID
     */
    @Column(name = "updated_by", nullable = false)
    private Long updatedBy;
    
    /**
     * JPA 生命周期回调：持久化前设置创建时间
     */
    @PrePersist
    protected void onCreate() {
        long timestamp = System.currentTimeMillis();
        if (createdAt == null) {
            createdAt = timestamp;
        }
        if (updatedAt == null) {
            updatedAt = timestamp;
        }
    }
    
    /**
     * JPA 生命周期回调：更新前设置更新时间
     */
    @PreUpdate
    protected void onUpdate() {
        updatedAt = System.currentTimeMillis();
    }
}

