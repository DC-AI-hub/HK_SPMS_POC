package com.spms.backend.repository.entities.sys;

import com.spms.backend.repository.entities.BaseEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@Table(name = "system_config_history")
public class SystemConfigHistoryEntity extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "config_id", nullable = false)
    private SystemConfigEntity config;

    @Column(name = "old_value", columnDefinition = "TEXT")
    private String oldValue;

    @Column(name = "new_value", nullable = false, columnDefinition = "TEXT")
    private String newValue;

    @Column(name = "change_type", nullable = false, length = 20)
    private String changeType;

    @Column(name = "changed_by", nullable = false, length = 255)
    private String changedBy;

    @Column(name = "change_timestamp")
    private LocalDateTime changeTimestamp;

    @PrePersist
    protected void onCreate() {
        if (changeTimestamp == null) {
            changeTimestamp = LocalDateTime.now();
        }
    }
}
