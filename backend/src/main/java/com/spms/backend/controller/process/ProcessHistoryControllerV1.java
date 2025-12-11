package com.spms.backend.controller.process;

import com.spms.backend.controller.BaseController;
import com.spms.backend.controller.dto.process.ProcessHistoryDTO;
import com.spms.backend.service.idm.UserService;
import com.spms.backend.service.model.idm.UserModel;
import com.spms.backend.service.model.process.ProcessHistoryModel;
import com.spms.backend.service.process.ProcessHistoryService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controller for accessing process history information.
 * Version 1 of the API.
 */
@RestController
@RequestMapping("/api/v1/process/history")
public class ProcessHistoryControllerV1 extends BaseController {
    
    private final ProcessHistoryService processHistoryService;

    private final UserService userService;

    /**
     * Constructs a new ProcessHistoryControllerV1 with the required service.
     * 
     * @param processHistoryService the process history service
     */
    public ProcessHistoryControllerV1(ProcessHistoryService processHistoryService, UserService userService) {
        this.processHistoryService = processHistoryService;
        this.userService = userService;
    }

    /**
     * Retrieves a paginated history of processes started by a specific user.
     * 
     * @param userId   ID of the user who started the processes
     * @param pageable Pagination information
     * @return Page of process history DTOs
     */
    @GetMapping("/started-by-user")
    public Page<ProcessHistoryDTO> getHistoryStartedByUser(
            @RequestParam Long userId,
            Pageable pageable) {
        return processHistoryService.getHistoryStartedByUser(userId, pageable)
                .map(this::convertToDTO);
    }

    /**
     * Retrieves a paginated history of processes handled by a specific user.
     * Admin API
     * 
     * @param userId   ID of the user who handled the processes
     * @param pageable Pagination information
     * @return Page of process history DTOs
     */
    @GetMapping("/handled-by-user")
    public Page<ProcessHistoryDTO> getHistoryHandledByUser(
            @RequestParam Long userId,
            Pageable pageable) {
        return processHistoryService.getHistoryHandledByUser(userId, pageable)
                .map(this::convertToDTO);
    }


    @GetMapping("/current-user/handle")
    public Page<ProcessHistoryDTO> getHistoryHandledByCurrentUser(
            Pageable pageable) {
        long userId = userService.getCurrentUserId();

        return processHistoryService.getHistoryHandledByUser(userId, pageable)
                .map(this::convertToDTO);
    }

    @GetMapping("/current-user/started")
    public Page<ProcessHistoryDTO> getHistoryStartedByCurrentUser(
            Pageable pageable) {
        long userId = userService.getCurrentUserId();
        return processHistoryService.getHistoryStartedByUser(userId, pageable)
                .map(this::convertToDTO);
    }

    /**
     * Converts a ProcessHistoryModel to a ProcessHistoryDTO.
     *
     *
     * @param model the process history model
     * @return the corresponding DTO
     */
    private ProcessHistoryDTO convertToDTO(ProcessHistoryModel model) {
        ProcessHistoryDTO dto = new ProcessHistoryDTO();
        dto.setProcessInstanceId(model.getProcessInstanceId());
        dto.setStartTime(model.getStartTime());
        dto.setEndTime(model.getEndTime());
        dto.setStartUserId(model.getStartUserId());
        dto.setBusinessKey(model.getBusinessKey());
        return dto;
    }
}
