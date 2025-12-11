package com.spms.backend.controller.idm;

import com.spms.backend.controller.BaseController;
import com.spms.backend.controller.dto.idm.UpdateUserRequest;
import com.spms.backend.controller.dto.idm.UserDTO;
import com.spms.backend.repository.entities.idm.DepartmentType;
import com.spms.backend.service.idm.DepartmentService;
import com.spms.backend.service.idm.OrganizationService;
import com.spms.backend.service.idm.RoleService;
import com.spms.backend.service.idm.UserService;
import com.spms.backend.service.model.idm.DepartmentModel;
import com.spms.backend.service.model.idm.RoleModel;
import com.spms.backend.service.model.idm.UserModel;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/users")
public class UserController extends BaseController {
    private final UserService userService;
    private final OrganizationService organizationService;
    private final RoleService roleService;
    private final DepartmentService departmentService;

    public UserController(UserService userService,
        RoleService roleService,
        OrganizationService organizationService,
                          DepartmentService departmentService
    ) {
        this.userService = userService;
        this.departmentService = departmentService;
        this.organizationService = organizationService;
        this.roleService = roleService;
    }

    @PostMapping
//    @PreAuthorize("hasAuthority('user:write')")
    public ResponseEntity<UserDTO> createUser(@RequestBody UserDTO userDTO) {
        return ResponseEntity.ok(
            UserDTO.fromUserModel(
                userService.createUser(userDTO.toUserModel())
            )
        );
    }

    @GetMapping("/{id}")
//    @PreAuthorize("hasAuthority('user:read')")
    public ResponseEntity<UserDTO> getUser(@PathVariable Long id) {
        UserModel userModel = userService.getUserById(id);
        UserDTO userDTO = UserDTO.fromUserModel(userModel);


        return ResponseEntity.ok(
                userDTO
        );
    }

    @PutMapping("/{id}")
//    @PreAuthorize("hasAuthority('user:write')")
    @Transactional
    public ResponseEntity<UserDTO> updateUser(
            @PathVariable Long id,
            @RequestBody UpdateUserRequest userDTO) {

        //TODO: re-factory to organization service
        UserModel updatedUser = userService.updateUser(id, userDTO.toUserModel());
        List<DepartmentModel> departments = departmentService.getUserDepartment(updatedUser);
        departments.stream().filter(x->x.getType()==DepartmentType.FUNCTIONAL || x.getType() == DepartmentType.LOCAL)
                .forEach(
                        x-> departmentService.deleteUserFromDepartment(x,List.of(updatedUser))
                );
        if(userDTO.getFunctionalDepartment()!=null ){
            Long funcId = userDTO.getFunctionalDepartment().getId();
            Optional<DepartmentModel> func = departmentService.getDepartmentById(funcId);
            if(func.isPresent() && func.get().getType() == DepartmentType.FUNCTIONAL){
                departmentService.addUserToDepartment(
                        func.get(),
                        List.of(updatedUser)
                );
            }
        }

        if(userDTO.getLocalDepartment() !=null ) {
            Long localId = userDTO.getLocalDepartment().getId();
            Optional<DepartmentModel> local = departmentService.getDepartmentById(localId);
            if (local.isPresent() &&
                    local.get().getType() == DepartmentType.LOCAL) {
                departmentService.addUserToDepartment(
                        local.get(),
                        List.of(updatedUser)
                );
            }
        }

        return ResponseEntity.ok(
                UserDTO.fromUserModel(
                    userService.getUserById(id)
                )
        );
    }

    @DeleteMapping("/{id}")
//    @PreAuthorize("hasAuthority('user:write')")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{userId}/roles/{roleId}")
//    @PreAuthorize("hasAuthority('user:manage')")
    public ResponseEntity<UserDTO> assignRole(
            @PathVariable Long userId,
            @PathVariable Long roleId) {

        UserModel user = userService.getUserById(userId);
        RoleModel role = roleService.getRoleById(roleId);
        if (user == null || role == null) {
            return ResponseEntity.notFound().build();
        }

        if (organizationService.assignRoles(user, List.of(role))) {
            return ResponseEntity.ok(
                    UserDTO.fromUserModel(
                            userService.getUserById(userId)
                    )
            );
        }
        return ResponseEntity.status(HttpStatus.NOT_MODIFIED).build();
    }

    @DeleteMapping("/{userId}/roles/{roleId}")
//    @PreAuthorize("hasAuthority('user:manage')")
    public ResponseEntity<UserDTO> removeRole(
            @PathVariable Long userId,
            @PathVariable Long roleId) {
        UserModel user = userService.getUserById(userId);
        RoleModel role = roleService.getRoleById(roleId);
        if (user == null || role == null) {
            return ResponseEntity.notFound().build();
        }

        if (organizationService.unassignRoles(user, List.of(role))) {
            return ResponseEntity.ok(
                    UserDTO.fromUserModel(
                            userService.getUserById(userId)
                    )
            );
        }
        return ResponseEntity.status(HttpStatus.NOT_MODIFIED).build();
    }


    @GetMapping("/search")
//    @PreAuthorize("hasAuthority('user:read')")
    public ResponseEntity<List<UserDTO>> searchUsers(
            @RequestParam String query, Pageable pageable
        ) {

        return ResponseEntity.ok(
            userService.searchUsers(query,pageable).stream()
                .map(UserDTO::fromUserModel)
                .collect(Collectors.toList())
        );
    }
}
