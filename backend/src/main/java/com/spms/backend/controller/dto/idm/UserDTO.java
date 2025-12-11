package com.spms.backend.controller.dto.idm;

import com.spms.backend.repository.entities.idm.User;
import com.spms.backend.service.model.idm.UserModel;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Getter
@Setter
@ToString
public class UserDTO {

    private Long id;
    @NotBlank(message = "Username is required")
    private String username;
    @NotBlank(message = "Email is required")
    private String email;
    private String description;
    private String type;
    private Map<String, String> userProfiles;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String createdBy;
    private String modifiedBy;
    private DepartmentDTO functionalDepartment;
    private DepartmentDTO localDepartment;
    private List<DepartmentDTO> allDepartments;
    private List<RoleDTO> roles;

    // Convenience methods for profile fields
    public String getFirstName() {
        return userProfiles != null ? userProfiles.get("firstName") : null;
    }

    public void setFirstName(String firstName) {
        if (userProfiles != null) {
            userProfiles.put("firstName", firstName);
        }
    }

    public String getLastName() {
        return userProfiles != null ? userProfiles.get("lastName") : null;
    }

    public void setLastName(String lastName) {
        if (userProfiles != null) {
            userProfiles.put("lastName", lastName);
        }
    }

    public String getAvatarUrl() {
        return userProfiles != null ? userProfiles.get("avatarUrl") : null;
    }

    public void setAvatarUrl(String avatarUrl) {
        if (userProfiles != null) {
            userProfiles.put("avatarUrl", avatarUrl);
        }
    }

    public UserModel toUserModel() {
        UserModel model = new UserModel();
        model.setUsername(username);
        model.setEmail(email);
        model.setDescription(description);
        model.setUserProfiles(userProfiles);
        model.setCreatedAt(createdAt);
        model.setUpdatedAt(updatedAt);
        model.setCreatedBy(createdBy);
        model.setModifiedBy(modifiedBy);
        model.setType(User.UserType.of(type));
        return model;
    }

    public static UserDTO fromUserModel(UserModel model) {
        UserDTO dto = new UserDTO();
        dto.setId(model.getId());
        dto.setUsername(model.getUsername());
        dto.setEmail(model.getEmail());
        dto.setDescription(model.getDescription());
        dto.setUserProfiles(model.getUserProfiles());
        dto.setCreatedAt(model.getCreatedAt());
        dto.setUpdatedAt(model.getUpdatedAt());
        dto.setCreatedBy(model.getCreatedBy());
        dto.setModifiedBy(model.getModifiedBy());
        dto.setType(model.getType().name());
        dto.setAllDepartments(model.getDepartments().stream().map(DepartmentDTO::new).toList());
        dto.setRoles(model.getRoles().stream().map(RoleDTO::fromRoleModel).toList());
        if (model.getFunctionalDepartment() != null) {
            dto.setFunctionalDepartment(new DepartmentDTO(model.getFunctionalDepartment()));
        }
        if (model.getLocalDepartment() != null) {
            dto.setLocalDepartment(new DepartmentDTO(model.getLocalDepartment()));
        }
        return dto;
    }
}
