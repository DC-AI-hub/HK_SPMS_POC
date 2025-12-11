package com.spms.backend.service.process.impl;

import com.spms.backend.service.idm.UserService;
import com.spms.backend.service.model.idm.UserModel;
import com.spms.backend.service.model.process.ProcessInstanceConverter;
import com.spms.backend.service.model.process.ProcessInstanceModel;
import com.spms.backend.service.model.process.TaskModel;
import com.spms.backend.service.process.UserInstanceProcessService;
import org.flowable.engine.HistoryService;
import org.flowable.engine.ProcessEngine;
import org.flowable.engine.RuntimeService;
import org.flowable.engine.TaskService;
import org.flowable.engine.history.HistoricProcessInstanceQuery;
import org.flowable.engine.runtime.ProcessInstanceQuery;
import org.flowable.task.api.TaskQuery;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserInstanceProcessServiceImpl implements UserInstanceProcessService {

    private final UserService userService;
    private final RuntimeService runtimeService;
    private final TaskService taskService;
    private final HistoryService historyService;

    public UserInstanceProcessServiceImpl(UserService userService, ProcessEngine flowableEngine) {
        this.userService = userService;
        this.runtimeService = flowableEngine.getRuntimeService();
        this.taskService = flowableEngine.getTaskService();
        this.historyService = flowableEngine.getHistoryService();
    }

    /**
     * Retrieves a paginated list of active process instances where the specified user is involved.
     *
     * @param userId   The ID of the user whose related instances are to be fetched.
     * @param pageable Pagination information (page number, size, sorting).
     * @return A page of {@link ProcessInstanceModel} representing active process instances.
     */
    @Override
    public Page<ProcessInstanceModel> getUserRelatedInstanceService(Long userId, Pageable pageable) {

        UserModel user = userService.getUserById(userId);
        String username = user.getUsername();

        ProcessInstanceQuery query = runtimeService.createProcessInstanceQuery()
                .involvedUser(username)
                .orderByStartTime().desc();

        long total = query.count();
        int firstResult = (int) pageable.getOffset();
        int maxResults = pageable.getPageSize();

        List<ProcessInstanceModel> instances = query.listPage(firstResult, maxResults).stream()
                .map(ProcessInstanceConverter::convert)
                .collect(Collectors.toList());

        return new PageImpl<>(instances, pageable, total);
    }

    /**
     * Fetches a paginated list of active tasks assigned to the specified user.
     *
     * @param userId   The ID of the user whose assigned tasks are to be retrieved.
     * @param pageable Pagination information (page number, size, sorting).
     * @return A page of {@link TaskModel} representing the user's active tasks.
     */
    @Override
    public Page<TaskModel> getAssignedRunningUserTasks(Long userId, Pageable pageable) {

        UserModel user = userService.getUserById(userId);
        String username = user.getUsername();

        TaskQuery query = taskService.createTaskQuery()
                .taskAssignee(username)
                .active()
                .orderByTaskCreateTime().desc();

        long total = query.count();
        int firstResult = (int) pageable.getOffset();
        int maxResults = pageable.getPageSize();

        List<TaskModel> tasks = query.listPage(firstResult, maxResults).stream()
                .map(ProcessInstanceConverter::convert)
                .collect(Collectors.toList());

        return new PageImpl<>(tasks, pageable, total);
    }

    /**
     * Retrieves a paginated list of historical process instances where the specified user was involved.
     *
     * @param userId   The ID of the user whose historical instances are to be fetched.
     * @param pageable Pagination information (page number, size, sorting).
     * @return A page of {@link ProcessInstanceModel} representing historical process instances.
     */
    @Override
    public Page<ProcessInstanceModel> getUserRelatedHistoryInstanceService(Long userId, Pageable pageable) {
        UserModel user = userService.getUserById(userId);
        String username = user.getUsername();

        HistoricProcessInstanceQuery query = historyService.createHistoricProcessInstanceQuery()
                .involvedUser(username)
                .orderByProcessInstanceStartTime().desc();

        long total = query.count();
        int firstResult = (int) pageable.getOffset();
        int maxResults = pageable.getPageSize();

        List<ProcessInstanceModel> instances = query.listPage(firstResult, maxResults).stream()
                .map(ProcessInstanceConverter::convert)
                .collect(Collectors.toList());

        return new PageImpl<>(instances, pageable, total);
    }
}
