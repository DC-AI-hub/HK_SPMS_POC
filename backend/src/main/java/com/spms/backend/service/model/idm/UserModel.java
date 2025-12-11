package com.spms.backend.service.model.idm;

import com.spms.backend.repository.entities.idm.Department;
import com.spms.backend.repository.entities.idm.DepartmentType;
import com.spms.backend.repository.entities.idm.User;
import com.spms.backend.service.BaseModel;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Getter
@Setter
public class UserModel extends BaseModel<User> {

    private Long id;

    @NotBlank(message = "Username is required")
    private String username;

    @NotBlank(message = "Email is required")
    private String email;

    @NotBlank(message = "Provider is required")
    private String provider;
    //@NotBlank(message = "Provider ID is required")
    private String providerId;

    @NotNull(message = "User type is required")
    private User.UserType type;

    private String description;
    private Map<String, String> userProfiles;
    private Set<RoleModel> roles = new HashSet<>();
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String createdBy;
    private List<DepartmentModel> departments = new ArrayList<>();
    private DepartmentModel functionalDepartment;
    private DepartmentModel localDepartment;
    private String modifiedBy;

    @Override
    public User toEntityForCreate() {
        User user = toEntityForUpdate();
        user.setId(null);
        user.setCreatedAt(LocalDateTime.now());
        user.setCreatedBy(createdBy);
        return user;
    }

    @Override
    public User toEntityForUpdate() {
        User user = new User();
        user.setId(id);
        user.setUsername(username);
        user.setEmail(email);
        if (StringUtils.hasLength(provider)) {
            user.setProvider(provider);
        } else {
            user.setProvider("keycloak");
        }
        user.setDepartments(new HashSet<>());
        user.setProviderId(providerId);
        user.setType(type);
        user.setDescription(description);
        user.setUpdatedAt(LocalDateTime.now());
        user.setModifiedBy(modifiedBy);
        // Convert userProfiles map to JSON string for storage
        if (userProfiles != null) {
            user.setUserProfiles(userProfiles);
        }
        return user;
    }

    public static UserModel fromEntity(User user,Map<String,String> userProfiles) {
        UserModel model = fromEntity(user);
        model.setUserProfiles(userProfiles);
        return model;
    }

    public static UserModel fromEntity(User user) {
        UserModel model = new UserModel();
        model.setId(user.getId());
        model.setUsername(user.getUsername());
        model.setEmail(user.getEmail());
        model.setProvider(user.getProvider());
        model.setProviderId(user.getProviderId());
        model.setType(user.getType());
        model.setDescription(user.getDescription());
        model.setCreatedAt(user.getCreatedAt());
        model.setUpdatedAt(user.getUpdatedAt());
        model.setCreatedBy(user.getCreatedBy());
        model.setModifiedBy(user.getModifiedBy());
        model.setRoles(user.getRoles().stream().map(RoleModel::fromEntity).collect(Collectors.toSet()));
        if(user.getDepartments()!=null && !user.getDepartments().isEmpty()) {
            model.setDepartments(
                    user.getDepartments().stream().map(DepartmentModel::new).toList()
            );
           Optional<Department> functional =  user.getDepartments().stream().filter(x->x.getType() == DepartmentType.FUNCTIONAL).findFirst();
           functional.ifPresent(x->model.setFunctionalDepartment(new DepartmentModel(x)));
           Optional<Department> local =  user.getDepartments().stream().filter(x->x.getType() == DepartmentType.LOCAL).findFirst();
           local.ifPresent(x->model.setLocalDepartment(new DepartmentModel(x)));
        }
        // Convert JSON string back to map
        if (user.getUserProfiles() != null) {
            model.setUserProfiles(user.getUserProfiles());
        }
        return model;
    }
}
