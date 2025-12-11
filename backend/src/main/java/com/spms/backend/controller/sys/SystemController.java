package com.spms.backend.controller.sys;

import com.fasterxml.jackson.core.type.TypeReference;
import com.spms.backend.config.SpmsOidcUser;
import com.spms.backend.controller.dto.idm.DepartmentDTO;
import com.spms.backend.controller.dto.idm.UserDTO;
import com.spms.backend.controller.dto.process.EngineCapabilityDto;
import com.spms.backend.controller.dto.process.EngineCapabilityListDto;
import com.spms.backend.repository.entities.idm.Role;
import com.spms.backend.service.idm.DepartmentService;
import com.spms.backend.service.model.process.EventCapabilityCategory;
import com.spms.backend.service.model.process.TaskCapabilityCategory;
import com.spms.backend.service.process.EngineCapabilityService;
import com.spms.backend.service.sys.MenuService;
import com.spms.backend.repository.idm.RoleRepository;
import org.flowable.spring.security.UserDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.spms.backend.controller.BaseController;
import com.spms.backend.controller.dto.sys.LoginInfo;
import jakarta.validation.Valid;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/system")
public class SystemController extends BaseController {

    @Autowired
    private ResourceLoader resourceLoader;

    @Autowired
    private EngineCapabilityService capabilityService;

    @Autowired
    private DepartmentService departmentService;

    @Autowired
    private MenuService menuService;
    

    private final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * Returns the application menu structure based on user roles
     * Uses the new dynamic menu service while maintaining backward compatibility
     *
     * @param user The authenticated OIDC user
     * @return JSON array of menu items
     */
    @GetMapping("/menu")
    public String getMenu(@AuthenticationPrincipal OidcUser user) throws Exception {
        if (user == null) {
            return "[]";
        }

        // Use the new dynamic menu service to get menus for current user
        var menuModels = menuService.getMenusForCurrentUser();
        
        // Convert to the legacy JSON format for backward compatibility
        var filteredMenu = new ArrayList<>();
        for (var menuModel : menuModels) {
            var menuItem = objectMapper.createObjectNode();
            menuItem.put("title", menuModel.getTitle());
            menuItem.put("path", menuModel.getPath());
            menuItem.put("icon", menuModel.getIcon());
            menuItem.put("displayOrder", menuModel.getDisplayOrder());
            menuItem.put("active", menuModel.getActive());
            
            // Convert role IDs to role names for compatibility
            var rolesArray = objectMapper.createArrayNode();
            if (menuModel.getAllowedRoleIds() != null) {
                for (var roleId : menuModel.getAllowedRoleIds()) {
                    rolesArray.add("ROLE_" + roleId); // Maintain legacy role format
                }
            }
            menuItem.set("roles", rolesArray);
            
            filteredMenu.add(menuItem);
        }

        return objectMapper.writeValueAsString(filteredMenu);
    }

    @GetMapping("/login-info")
    public LoginInfo getLoginInfo(@AuthenticationPrincipal SpmsOidcUser user) {
        LoginInfo info = new LoginInfo();
        if (user != null) {

            info.setId(user.getAuthenticatedUser().getId());
            info.setUsername(user.getPreferredUsername());
            info.setEmail(user.getEmail());
            info.setFirstName(user.getGivenName());
            info.setLastName(user.getFamilyName());
            info.setDepartments(departmentService.getUserDepartment(user.getAuthenticatedUser()).stream().map(DepartmentDTO::new).toList());

            // 安全获取用户角色信息
            Object rolesObj = user.getUserInfo().getClaims().get("roles");
            if (rolesObj instanceof List) {
                List<String> rolesList = (List<String>) rolesObj;
                info.setRoles(rolesList.stream()
                        .map(a -> new Role(a.toUpperCase()))
                        .collect(Collectors.toList()));
            } else {
                // 如果没有角色信息，设置一个默认角色或空列表
                info.setRoles(new ArrayList<>());
            }
            info.setUserType(user.getAuthenticatedUser().getType().name());
        }
        return info;
    }

    /**
     * Returns frontend configuration values
     *
     * @return JSON object containing frontend settings
     */
    @GetMapping("/config")
    public String getFrontendConfig() throws Exception {
        Resource resource = resourceLoader.getResource("classpath:system-config.json");
        var config = objectMapper.readTree(resource.getInputStream());
        return config.get("frontend").toString();
    }

    @GetMapping("/health")
    public String healthCheck() {
        return "{\"status\": \"UP\"}";
    }

    /**
     * Logout endpoint - Handles user logout
     *
     * @return ResponseEntity with HTTP status
     */
    @PostMapping("/logout")
    public ResponseEntity<Void> logout(HttpServletRequest request, HttpServletResponse response) {
        try {
            // Invalidate the session
            SecurityContextHolder.clearContext();
            HttpSession session = request.getSession(false);
            if (session != null) {
                session.invalidate();
            }

            // Clear authentication
            SecurityContextHolder.getContext().setAuthentication(null);

            // Clear cookies
            Cookie[] cookies = request.getCookies();
            if (cookies != null) {
                for (Cookie cookie : cookies) {
                    cookie.setMaxAge(0);
                    response.addCookie(cookie);
                }
            }

            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }


    @GetMapping("/capabilities")
    public ResponseEntity<EngineCapabilityListDto> getEngineCapabilities() {
        EngineCapabilityListDto dto = new EngineCapabilityListDto();
        dto.setEvents(new ArrayList<>());
        dto.setTasks(new ArrayList<>());
        capabilityService.getCapabilities().forEach(
                x -> {
                    EngineCapabilityDto capabilityDto = new EngineCapabilityDto();
                    capabilityDto.setName(x.getName());
                    capabilityDto.setDescription(x.getDescription());
                    capabilityDto.setEnabled(x.isEnabled());

                    EventCapabilityCategory ecategory = EventCapabilityCategory.of(x.getName());
                    if (ecategory != EventCapabilityCategory.UNKNOWN) {
                        capabilityDto.setCategory(ecategory.name());
                        dto.getEvents().add(capabilityDto);
                    }

                    TaskCapabilityCategory tcategory = TaskCapabilityCategory.of(x.getName());
                    if (tcategory != TaskCapabilityCategory.UNKNOWN) {
                        capabilityDto.setCategory(tcategory.name());
                        dto.getTasks().add(capabilityDto);
                    }
                }
        );
        return ResponseEntity.ok(dto);
    }
    
}
