package com.spms.backend.controller.idm;

import com.spms.backend.controller.dto.idm.CreateDepartmentRequestDTO;
import com.spms.backend.controller.dto.idm.DepartmentDTO;
import com.spms.backend.controller.dto.idm.UpdateDepartmentRequestDTO;
import com.spms.backend.controller.dto.idm.UserDepartmentRequestDTO;
import com.spms.backend.controller.dto.idm.UserDepartmentsResponseDTO;
import jakarta.persistence.EntityNotFoundException;
import com.spms.backend.repository.entities.idm.DepartmentType;
import com.spms.backend.service.idm.DepartmentService;
import com.spms.backend.service.idm.UserService;
import com.spms.backend.service.model.idm.DepartmentModel;
import com.spms.backend.service.model.idm.UserModel;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Controller for handling Department related operations
 * Provides CRUD endpoints for Department management
 */
@RestController
@RequestMapping("/api/v1/departments")
public class DepartmentController {

    private final DepartmentService departmentService;

    private final UserService userService;

    public DepartmentController(DepartmentService departmentService,
                                UserService userService
    ) {
        this.departmentService = departmentService;
        this.userService = userService;
    }

    /**
     * Retrieves a paginated list of departments
     *
     * @param pageable Pagination parameters
     * @param search   Optional search term to filter departments
     * @return Page of DepartmentDTO objects
     */
    @GetMapping
    public Page<DepartmentDTO> listDepartments(Pageable pageable,
                                               @RequestParam(required = false) String search,
                                               @RequestParam(required = false) String type
    ) {
        try {
            var dpt = DepartmentType.valueOf(type);
            return departmentService.listDepartments(pageable, search,dpt)  .map(DepartmentDTO::new);
        } catch (Exception ignore){

        }
        return departmentService.listDepartments(pageable, search)
                .map(DepartmentDTO::new);
    }


    /**
     * Creates a new department
     *
     * @param requestDTO DTO containing department creation data
     * @return Created DepartmentDTO
     */
    @PostMapping
    public ResponseEntity<DepartmentDTO> createDepartment(@RequestBody CreateDepartmentRequestDTO requestDTO) {

        if (departmentService.isValidParent(requestDTO.getLevel(), requestDTO.getParent(), requestDTO.getType())) {
            DepartmentModel departmentModel = new DepartmentModel();
            departmentModel.setName(requestDTO.getName());
            departmentModel.setParent(requestDTO.getParent());
            departmentModel.setType(requestDTO.getType());
            departmentModel.setTags(requestDTO.getTags());
            departmentModel.setLevel(requestDTO.getLevel());
            if (departmentModel.getDepartmentHead() != null) {
                UserModel departmentHead = userService.getUserById(requestDTO.getDepartmentHeadId());
                if (departmentHead != null) {
                    departmentModel.setDepartmentHead(
                            departmentHead
                    );
                }
            }
            DepartmentModel created = departmentService.createDepartment(departmentModel);
            return ResponseEntity.ok(new DepartmentDTO(created));
        }

        return ResponseEntity.badRequest().build();
    }


    /**
     * Add users to a department
     *
     * @param departmentId The ID of the department
     * @param requestDTO   DTO containing the list of user IDs to add
     * @return ResponseEntity indicating success or failure
     */
    @PostMapping("/{departmentId}/users")
    public ResponseEntity<Void> addUsersToDepartment(@PathVariable Long departmentId,
                                                     @RequestBody UserDepartmentRequestDTO requestDTO) {
        Optional<DepartmentModel> departmentOpt = departmentService.getDepartmentById(departmentId);
        if (departmentOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        List<UserModel> users = new ArrayList<>();
        for (Long userId : requestDTO.getUserIds()) {
            UserModel user = userService.getUserById(userId);
            if (user == null) {
                return ResponseEntity.badRequest().build();
            }
            users.add(user);
        }

        boolean success = departmentService.addUserToDepartment(departmentOpt.get(), users);
        return success ? ResponseEntity.ok().build() : ResponseEntity.badRequest().build();
    }

    /**
     * Remove users from a department
     *
     * @param departmentId The ID of the department
     * @param requestDTO   DTO containing the list of user IDs to remove
     * @return ResponseEntity indicating success or failure
     */
    @DeleteMapping("/{departmentId}/users")
    public ResponseEntity<Void> removeUsersFromDepartment(@PathVariable Long departmentId,
                                                          @RequestBody UserDepartmentRequestDTO requestDTO) {
        Optional<DepartmentModel> departmentOpt = departmentService.getDepartmentById(departmentId);
        if (departmentOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        List<UserModel> users = new ArrayList<>();
        for (Long userId : requestDTO.getUserIds()) {
            UserModel user = userService.getUserById(userId);
            if (user == null) {
                return ResponseEntity.badRequest().build();
            }
            users.add(user);
        }

        boolean success = departmentService.deleteUserFromDepartment(departmentOpt.get(), users);
        return success ? ResponseEntity.noContent().build() : ResponseEntity.badRequest().build();
    }

    /**
     * Updates an existing department
     *
     * @param id ID of the department to update
     * @param requestDTO DTO containing updated department data
     * @return Updated DepartmentDTO
     */
    @PutMapping("/{id}")
    public ResponseEntity<DepartmentDTO> updateDepartment(@PathVariable Long id,
                                                          @RequestBody UpdateDepartmentRequestDTO requestDTO) {
        // Validate request
        if (requestDTO.getName() == null || requestDTO.getName().trim().isEmpty()) {
            return ResponseEntity.badRequest().build();
        }
        if (requestDTO.getType() == null) {
            return ResponseEntity.badRequest().build();
        }

        // Convert DTO to model
        DepartmentModel departmentModel = new DepartmentModel();
        departmentModel.setName(requestDTO.getName());
        departmentModel.setTags(requestDTO.getTags());
        departmentModel.setParent(requestDTO.getParent());
        departmentModel.setType(requestDTO.getType());
        departmentModel.setLevel(requestDTO.getLevel());
        departmentModel.setActive(requestDTO.isActive());
        
        // Handle department head
        if (requestDTO.getDepartmentHeadId() != null) {
            UserModel departmentHead = userService.getUserById(requestDTO.getDepartmentHeadId());
            if (departmentHead == null) {
                return ResponseEntity.badRequest().build();
            }
            departmentModel.setDepartmentHead(departmentHead);
        }

        try {
            DepartmentModel updatedDepartment = departmentService.updateDepartment(id, departmentModel);
            return ResponseEntity.ok(new DepartmentDTO(updatedDepartment));
        } catch (EntityNotFoundException e) {
            return ResponseEntity.notFound().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }
}
