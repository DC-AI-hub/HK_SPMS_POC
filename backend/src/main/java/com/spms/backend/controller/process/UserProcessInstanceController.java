package com.spms.backend.controller.process;

import com.spms.backend.controller.dto.process.ProcessInstanceDTO;
import com.spms.backend.controller.dto.process.TaskDTO;
import com.spms.backend.converter.ProcessInstanceConvertor;
import com.spms.backend.service.exception.SpmsRuntimeException;
import com.spms.backend.service.idm.UserService;
import com.spms.backend.service.model.process.ProcessInstanceModel;
import com.spms.backend.service.model.process.TaskModel;
import com.spms.backend.service.process.UserInstanceProcessService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/user/process-instances")
public class UserProcessInstanceController {

    private final UserInstanceProcessService userInstanceProcessService;
    private final UserService userService;
    private final ProcessInstanceConvertor processInstanceConvertor;

    @Autowired
    public UserProcessInstanceController(UserInstanceProcessService userInstanceProcessService,
                                         UserService userService,
                                         ProcessInstanceConvertor processInstanceConvertor) {
        this.userInstanceProcessService = userInstanceProcessService;
        this.userService = userService;
        this.processInstanceConvertor = processInstanceConvertor;
    }

    /**
     * Retrieves active process instances related to the current user
     *
     * @param pageable Pagination configuration
     * @return Page of ProcessInstanceDTO objects representing active instances
     * @throws SpmsRuntimeException if retrieval fails
     */
    @GetMapping("/active")
    public ResponseEntity<Page<ProcessInstanceDTO>> getActiveInstances(Pageable pageable) {
        try {
            Long currentUserId = userService.getCurrentUserId();
            Page<ProcessInstanceModel> models = userInstanceProcessService.getUserRelatedInstanceService(currentUserId, pageable);

            Page<ProcessInstanceDTO> dtos = models.map(
                    model -> processInstanceConvertor.convertToProcessInstanceDTO(model, true)
            );

            return ResponseEntity.ok(dtos);
        } catch (SpmsRuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * Retrieves assigned running tasks for the current user
     *
     * @param pageable Pagination configuration
     * @return Page of TaskDTO objects representing assigned tasks
     * @throws SpmsRuntimeException if retrieval fails
     */
    @GetMapping("/tasks")
    public ResponseEntity<Page<TaskDTO>> getAssignedTasks(Pageable pageable) {
        try {
            Long currentUserId = userService.getCurrentUserId();
            Page<TaskModel> models = userInstanceProcessService.getAssignedRunningUserTasks(currentUserId, pageable);

            Page<TaskDTO> dtos = models.map(processInstanceConvertor::convertToTaskDTO);

            return ResponseEntity.ok(dtos);
        } catch (SpmsRuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * Retrieves historical process instances related to the current user
     *
     * @param pageable Pagination configuration
     * @return Page of ProcessInstanceDTO objects representing historical instances
     * @throws SpmsRuntimeException if retrieval fails
     */
    @GetMapping("/history")
    public ResponseEntity<Page<ProcessInstanceDTO>> getHistoricalInstances(Pageable pageable) {
        try {
            Long currentUserId = userService.getCurrentUserId();
            Page<ProcessInstanceModel> models = userInstanceProcessService.getUserRelatedHistoryInstanceService(currentUserId, pageable);

            Page<ProcessInstanceDTO> dtos = models.map(
                    model ->
                            processInstanceConvertor.convertToProcessInstanceDTO(model, false));
            return ResponseEntity.ok(dtos);
        } catch (SpmsRuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }
}
