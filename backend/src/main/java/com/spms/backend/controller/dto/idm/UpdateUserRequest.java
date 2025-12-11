package com.spms.backend.controller.dto.idm;


import com.spms.backend.repository.entities.idm.User;
import com.spms.backend.service.model.idm.UserModel;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Data
public class UpdateUserRequest {

    private Long id;
    @NotBlank(message = "Username is required")
    private String username;
    @NotBlank(message = "Email is required")
    private String email;
    private String description;
    private String type;
    private Map<String, String> userProfiles;
    private DepartmentDTO functionalDepartment;
    private DepartmentDTO localDepartment;
    private List<RoleDTO> roles;



    public UserModel toUserModel() {
        UserModel model = new UserModel();
        model.setUsername(username);
        model.setEmail(email);
        model.setDescription(description);
        model.setUserProfiles(userProfiles);
        model.setType(User.UserType.of(type));
        return model;
    }
}
