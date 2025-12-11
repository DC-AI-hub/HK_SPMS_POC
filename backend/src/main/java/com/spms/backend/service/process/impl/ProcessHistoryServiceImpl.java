package com.spms.backend.service.process.impl;

import com.spms.backend.service.idm.UserService;
import com.spms.backend.service.model.idm.UserModel;
import com.spms.backend.service.process.ProcessHistoryService;
import com.spms.backend.service.model.process.ProcessHistoryModel;
import org.flowable.engine.HistoryService;
import org.flowable.engine.history.HistoricProcessInstance;
import org.flowable.engine.history.HistoricProcessInstanceQuery;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ProcessHistoryServiceImpl implements ProcessHistoryService {

    private static final Logger logger = LoggerFactory.getLogger(ProcessHistoryServiceImpl.class);

    private final HistoryService historyService;

    private final UserService userService;

    @Autowired
    public ProcessHistoryServiceImpl(HistoryService historyService,UserService userService) {
        this.historyService = historyService;
        this.userService= userService;
    }

    /**
     * Retrieves a paginated history of processes started by a specific user
     *
     * @param userId   ID of the user who started the processes
     * @param pageable Pagination information
     * @return Page of process history models
     */
    @Override
    public Page<ProcessHistoryModel> getHistoryStartedByUser(Long userId, Pageable pageable) {
        logger.debug("Fetching process history started by user: {}, page: {}", userId, pageable);

        UserModel userModel = userService.getUserById(userId);

        HistoricProcessInstanceQuery query = historyService.createHistoricProcessInstanceQuery()
                .variableValueEquals("initiator",userModel.getUsername())
                .orderByProcessInstanceStartTime().desc();

        long total = query.count();
        logger.info("Found {} processes started by user: {}", total, userId);
        
        int firstResult = (int) pageable.getOffset();
        int maxResults = pageable.getPageSize();
        
        List<HistoricProcessInstance> instances = query.listPage(firstResult, maxResults);
        List<ProcessHistoryModel> models = instances.stream()
                .map(this::toModel)
                .collect(Collectors.toList());
                
        logger.debug("Returning {} process history records for user: {}", models.size(), userId);
        return new PageImpl<>(models, pageable, total);
    }

    /**
     * Retrieves a paginated history of processes handled by a specific user
     *
     * @param userId   ID of the user who handled the processes
     * @param pageable Pagination information
     * @return Page of process history models
     */
    @Override
    public Page<ProcessHistoryModel> getHistoryHandledByUser(Long userId, Pageable pageable) {
        logger.debug("Fetching process history handled by user: {}, page: {}", userId, pageable);
        UserModel userModel = userService.getUserById(userId);

        HistoricProcessInstanceQuery query = historyService.createHistoricProcessInstanceQuery()
                .involvedUser(userModel.getUsername())
                .orderByProcessInstanceStartTime().desc();

        long total = query.count();
        logger.info("Found {} processes handled by user: {}", total, userId);
        
        int firstResult = (int) pageable.getOffset();
        int maxResults = pageable.getPageSize();
        
        List<HistoricProcessInstance> instances = query.listPage(firstResult, maxResults);
        List<ProcessHistoryModel> models = instances.stream()
                .map(this::toModel)
                .collect(Collectors.toList());
                
        logger.debug("Returning {} process history records for user: {}", models.size(), userId);
        return new PageImpl<>(models, pageable, total);
    }

    private ProcessHistoryModel toModel(HistoricProcessInstance instance) {
        ProcessHistoryModel model = new ProcessHistoryModel();
        model.setProcessInstanceId(instance.getId());
        model.setStartTime(instance.getStartTime());
        model.setEndTime(instance.getEndTime());
        model.setStartUserId(instance.getStartUserId());
        model.setBusinessKey(instance.getBusinessKey());
        return model;
    }
}
