package com.spms.backend.controller.dto.sys;

import com.spms.backend.repository.entities.sys.UserActivity;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;

@Schema(description = "User activity data transfer object")
public class UserActivityDTO {
    @Schema(description = "Activity ID")
    private Long id;
    
    @Schema(description = "User ID who performed the activity")
    private Long userId;
    
    @Schema(description = "Type of action performed")
    private String actionType;
    
    @Schema(description = "Type of entity affected")
    private String entityType;
    
    @Schema(description = "ID of entity affected")
    private Long entityId;
    
    @Schema(description = "Timestamp of activity")
    private LocalDateTime timestamp;
    
    @Schema(description = "Additional activity details in JSON format")
    private String details;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }
    public String getActionType() { return actionType; }
    public void setActionType(String actionType) { this.actionType = actionType; }
    public String getEntityType() { return entityType; }
    public void setEntityType(String entityType) { this.entityType = entityType; }
    public Long getEntityId() { return entityId; }
    public void setEntityId(Long entityId) { this.entityId = entityId; }
    public LocalDateTime getTimestamp() { return timestamp; }
    public void setTimestamp(LocalDateTime timestamp) { this.timestamp = timestamp; }
    public String getDetails() { return details; }
    public void setDetails(String details) { this.details = details; }

    public static UserActivityDTO fromEntity(UserActivity entity) {
        UserActivityDTO dto = new UserActivityDTO();
        dto.setId(entity.getId());
        dto.setUserId(entity.getUserId());
        dto.setActionType(entity.getActionType());
        dto.setEntityType(entity.getEntityType());
        dto.setEntityId(entity.getEntityId());
        dto.setTimestamp(LocalDateTime.ofInstant(
            Instant.ofEpochMilli(entity.getCreatedAt()), 
            ZoneId.systemDefault()));
        dto.setDetails(entity.getDetails());
        return dto;
    }

    public UserActivity toEntity() {
        UserActivity entity = new UserActivity();
        entity.setId(this.getId());
        entity.setUserId(this.getUserId());
        entity.setActionType(this.getActionType());
        entity.setEntityType(this.getEntityType());
        entity.setEntityId(this.getEntityId());
        entity.setCreatedAt(this.getTimestamp().atZone(ZoneId.systemDefault()).toInstant().toEpochMilli());
        entity.setDetails(this.getDetails());
        return entity;
    }
}
