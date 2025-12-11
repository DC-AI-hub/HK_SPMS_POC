package com.spms.backend.service.model;

import com.spms.backend.repository.entities.sys.SystemConfigHistoryEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SystemConfigHistoryModel {

    private Long id;
    private String configKey;
    private String oldValue;
    private String newValue;
    private String changeType;
    private String changedBy;
    private LocalDateTime changeTimestamp;

    // Conversion methods
    public static SystemConfigHistoryModel fromEntity(SystemConfigHistoryEntity entity) {
        return SystemConfigHistoryModel.builder()
                .id(entity.getId())
                .configKey(entity.getConfig().getKey())
                .oldValue(entity.getOldValue())
                .newValue(entity.getNewValue())
                .changeType(entity.getChangeType())
                .changedBy(entity.getChangedBy())
                .changeTimestamp(entity.getChangeTimestamp())
                .build();
    }

    public SystemConfigHistoryEntity toEntity() {
        SystemConfigHistoryEntity entity = new SystemConfigHistoryEntity();
        entity.setId(this.id);
        entity.setOldValue(this.oldValue);
        entity.setNewValue(this.newValue);
        entity.setChangeType(this.changeType);
        entity.setChangedBy(this.changedBy);
        entity.setChangeTimestamp(this.changeTimestamp);
        // config will be set separately in service layer
        return entity;
    }
}
