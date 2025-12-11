package com.spms.backend.service.model;

import com.spms.backend.repository.entities.sys.UserActivity;

/**
 * Model class representing user activity data for service layer operations.
 */
public class UserActivityModel extends BaseModel<UserActivity> {
    private Long id;
    private Long userId;
    private String actionType;
    private String entityType;
    private Long entityId;
    private String details;
    private long createdAt;

    /**
     * Converts a UserActivity entity to a UserActivityModel.
     * 
     * @param entity the UserActivity entity to convert
     * @return the converted UserActivityModel
     */
    public static UserActivityModel fromEntity(UserActivity entity) {
        UserActivityModel model = new UserActivityModel();
        model.setId(entity.getId());
        model.setUserId(entity.getUserId());
        model.setActionType(entity.getActionType());
        model.setEntityType(entity.getEntityType());
        model.setEntityId(entity.getEntityId());
        model.setDetails(entity.getDetails());
        model.setCreatedAt(entity.getCreatedAt());
        return model;
    }

    // Getters and setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getActionType() {
        return actionType;
    }

    public void setActionType(String actionType) {
        this.actionType = actionType;
    }

    public String getEntityType() {
        return entityType;
    }

    public void setEntityType(String entityType) {
        this.entityType = entityType;
    }

    public Long getEntityId() {
        return entityId;
    }

    public void setEntityId(Long entityId) {
        this.entityId = entityId;
    }

    public String getDetails() {
        return details;
    }

    public void setDetails(String details) {
        this.details = details;
    }

    public long getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(long createdAt) {
        this.createdAt = createdAt;
    }

    @Override
    public UserActivity toEntityForCreate() {
        UserActivity entity = new UserActivity();
        entity.setUserId(this.userId);
        entity.setActionType(this.actionType);
        entity.setEntityType(this.entityType);
        entity.setEntityId(this.entityId);
        entity.setDetails(this.details);
        entity.setCreatedAt(this.createdAt);
        return entity;
    }

    @Override
    public UserActivity toEntityForUpdate() {
        UserActivity entity = new UserActivity();
        entity.setId(this.id);  // Include ID for update operations
        entity.setUserId(this.userId);
        entity.setActionType(this.actionType);
        entity.setEntityType(this.entityType);
        entity.setEntityId(this.entityId);
        entity.setDetails(this.details);
        entity.setCreatedAt(this.createdAt);
        return entity;
    }
}
