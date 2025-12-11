package com.spms.backend.service;

import com.spms.backend.repository.entities.BaseEntity;

public abstract class BaseModel<T extends BaseEntity> {

    public abstract T toEntityForUpdate();

    public abstract T toEntityForCreate();
}
