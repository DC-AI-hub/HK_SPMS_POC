package com.spms.backend.service.model;

/**
 * Base model class providing common functionality for all models.
 * @param <T> The entity type this model represents
 */
public abstract class BaseModel<T> {

    /**
     * Converts the model to an entity for create operations.
     * @return The created entity
     */
    public abstract T toEntityForCreate();

    /**
     * Converts the model to an entity for update operations.
     * @return The updated entity
     */
    public abstract T toEntityForUpdate();
}
