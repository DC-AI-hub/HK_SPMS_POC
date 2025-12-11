package com.spms.backend.service;


import com.spms.backend.repository.BaseRepository;
import com.spms.backend.repository.entities.BaseEntity;
import org.springframework.beans.factory.annotation.Autowired;

public abstract class BaseServiceImpl<T extends BaseEntity, R extends BaseRepository<T, Long>>
    implements BaseService {

    protected final R repository;

    @Autowired
    public BaseServiceImpl(R repository) {
        this.repository = repository;
    }
    
}
