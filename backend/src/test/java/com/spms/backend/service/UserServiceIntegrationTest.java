package com.spms.backend.service;


import com.spms.backend.config.SpmsOidcUser;
import com.spms.backend.repository.idm.DepartmentRepository;
import com.spms.backend.repository.idm.RoleRepository;
import com.spms.backend.service.exception.NotFoundException;
import com.spms.backend.service.exception.ValidationException;
import com.spms.backend.repository.entities.idm.User;
import com.spms.backend.repository.idm.UserRepository;
import com.spms.backend.service.idm.impl.UserServiceImpl;
import com.spms.backend.service.model.idm.UserModel;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.core.oidc.OidcIdToken;
import org.springframework.security.oauth2.core.oidc.OidcUserInfo;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.time.Instant;
import java.util.*;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@SpringBootTest(properties = "/application-test.properties")
public class UserServiceIntegrationTest {


    @MockitoBean
    private ClientRegistrationRepository clientRegistrationRepository; // Fixes the missing bean

    @Autowired
    private UserRepository userRepository;

    private UserServiceImpl userService;

    @Autowired
    private DepartmentRepository departmentRepository;

    @Autowired
    private RoleRepository roleRepository;


    @AfterEach
    void remove(){
        userService.repository.deleteAll();
    }

    @BeforeEach()
    void setUp() {
        userService = new UserServiceImpl(userRepository, departmentRepository,roleRepository);

        
        // Create test users
        User user1 = new User();
        user1.setUsername("john.doe");
        user1.setEmail("john@example.com");
        user1.setType(User.UserType.STAFF);
        user1.setDescription("Senior developer");
        user1.setProvider("keycloak");
        user1.setProviderId(UUID.randomUUID().toString());
        userRepository.save(user1);

        User user2 = new User();
        user2.setUsername("jane.smith");
        user2.setEmail("jane@example.com");
        user2.setType(User.UserType.VENDOR);
        user2.setProvider("keycloak");
        user2.setDescription("Marketing specialist");
        user2.setProviderId(UUID.randomUUID().toString());
        userRepository.save(user2);

        User user3 = new User();
        user3.setUsername("admin");
        user3.setEmail("admin@example.com");
        user3.setType(User.UserType.STAFF);
        user3.setProvider("keycloak");
        user3.setDescription("System administrator");
        user3.setProviderId(UUID.randomUUID().toString());
        userRepository.save(user3);

        // 1. Prepare minimal OIDC token
        Map<String, Object> claims = new HashMap<>();
        claims.put("sub", "123456");
        OidcIdToken idToken = new OidcIdToken(
                "mock-token",
                Instant.now(),
                Instant.now().plusSeconds(300),
                claims
        );

        // 2. Create simple dependencies
        GrantedAuthority authority = new SimpleGrantedAuthority("ROLE_USER");
        UserModel userModel = new UserModel(); // Add required user data
        userModel.setId(100L);
        userModel.setUsername("joe");

        // 3. Instantiate SpmsOidcUser
        SpmsOidcUser user = new SpmsOidcUser(
                Collections.singletonList(authority),
                idToken,
                new OidcUserInfo(claims), // or null if not needed
                "sub",
                userModel
        );

        Authentication auth = mock(Authentication.class);
        when(auth.getPrincipal()).thenReturn(user);
        SecurityContext context =  mock(SecurityContext.class);
        when(context.getAuthentication()).thenReturn(auth);
        SecurityContextHolder.setContext(context);
    }

    @Test
    void searchUsers_shouldReturnMatchingUsersByUsername() {
        Page<UserModel> result = userService.searchUsers("doe", PageRequest.of(0, 10));
        
        assertEquals(1, result.getTotalElements());
        assertEquals("john.doe", result.getContent().get(0).getUsername());
    }

    @Test
    void searchUsers_shouldReturnMatchingUsersByEmail() {
        Page<UserModel> result = userService.searchUsers("jane@example", PageRequest.of(0, 10));
        
        assertEquals(1, result.getTotalElements());
        assertEquals("jane.smith", result.getContent().get(0).getUsername());
    }

    @Test
    void searchUsers_shouldReturnMatchingUsersByDescription() {
        Page<UserModel> result = userService.searchUsers("developer", PageRequest.of(0, 10));
        
        assertEquals(1, result.getTotalElements());
        assertEquals("john.doe", result.getContent().get(0).getUsername());
    }

    @Test
    void searchUsers_shouldReturnAllUsersWhenQueryIsEmpty() {
        Page<UserModel> result = userService.searchUsers("", PageRequest.of(0, 10));
        
        assertEquals(3, result.getTotalElements());
    }

    @Test
    void searchUsers_shouldSupportPagination() {
        Page<UserModel> result = userService.searchUsers("", PageRequest.of(0, 2));
        
        assertEquals(3, result.getTotalElements());
        assertEquals(2, result.getContent().size());
    }

    @Test
    void searchUsers_shouldSupportSorting() {
        Page<UserModel> result = userService.searchUsers("", 
            PageRequest.of(0, 10, Sort.by("username").descending()));
        
        assertEquals(3, result.getTotalElements());
        assertEquals("jane.smith", result.getContent().get(1).getUsername());
        assertEquals("john.doe", result.getContent().get(0).getUsername());
    }

    @Test
    @WithMockUser(username = "test")
    void testCreateUser_validInput() {
        UserModel newUser = new UserModel();
        newUser.setUsername("new.user");
        newUser.setEmail("new@example.com");
        newUser.setType(User.UserType.STAFF);
        newUser.setProvider("keycloak");
        newUser.setProviderId(UUID.randomUUID().toString());
        
        UserModel created = userService.createUser(newUser);
        assertNotNull(created.getId());
        assertEquals("new.user", created.getUsername());
        assertEquals("new@example.com", created.getEmail());
    }

    @Test
    void testCreateUser_duplicateUsername() {
        UserModel newUser = new UserModel();
        newUser.setUsername("john.doe"); // Duplicate from setup
        newUser.setEmail("new@example.com");
        
        assertThrows(ValidationException.class, () -> {
            userService.createUser(newUser);
        });
    }

    @Test
    void testCreateUser_duplicateEmail() {
        UserModel newUser = new UserModel();
        newUser.setUsername("new.user");
        newUser.setEmail("john@example.com"); // Duplicate from setup
        
        assertThrows(ValidationException.class, () -> {
            userService.createUser(newUser);
        });
    }

    @Test
    void testCreateUser_nullValues() {
        UserModel newUser = new UserModel();
        newUser.setUsername(null);
        newUser.setEmail(null);
        
        assertThrows(NullPointerException.class, () -> {
            userService.createUser(newUser);
        });
    }

    @Test
    void testGetUserById_validId() {
        User existingUser = userRepository.findByUsername("john.doe");
        assertNotNull(existingUser, "Test user 'john.doe' should exist");
        UserModel result = userService.getUserById(existingUser.getId());
        
        assertEquals("john.doe", result.getUsername());
        assertEquals("john@example.com", result.getEmail());
        assertEquals(User.UserType.STAFF, result.getType());
    }

    @Test
    void testGetUserById_invalidId() {
        assertThrows(NotFoundException.class, () -> {
            userService.getUserById(-1L);
        });
    }

    @Test
    void testGetUserById_nonExistentId() {
        Long nonExistentId = 999999L;
        assertThrows(NotFoundException.class, () -> {
            userService.getUserById(nonExistentId);
        });
    }

    @Test
    void testGetUserById_verifyDataStructure() {
        User existingUser = userRepository.findByUsername("john.doe");
        assertNotNull(existingUser, "Test user 'john.doe' should exist");
        UserModel result = userService.getUserById(existingUser.getId());
        
        assertNotNull(result.getId());
        assertNotNull(result.getUsername());
        assertNotNull(result.getEmail());
        assertNotNull(result.getType());
        assertNotNull(result.getDescription());
        assertNotNull(result.getProvider());
        assertNotNull(result.getProviderId());
    }

    @Test
    @WithMockUser(username = "test")
    void testUpdateUser_validUpdate() {
        User existingUser = userRepository.findByUsername("john.doe");
        UserModel updateData = UserModel.fromEntity(existingUser);
        updateData.setEmail("john.new@example.com");
        updateData.setDescription("Lead developer");
        
        UserModel updated = userService.updateUser(existingUser.getId(), updateData);
        
        assertEquals("john.new@example.com", updated.getEmail());
        assertEquals("Lead developer", updated.getDescription());
        
        // Verify database state
        User dbUser = userRepository.findById(existingUser.getId()).orElseThrow();
        assertEquals("john.new@example.com", dbUser.getEmail());
        assertEquals("Lead developer", dbUser.getDescription());
    }

    @Test
    void testUpdateUser_nonExistentUser() {
        Long nonExistentId = 999999L;
        UserModel updateData = new UserModel();
        updateData.setEmail("nonexistent@example.com");
        
        assertThrows(NotFoundException.class, () -> {
            userService.updateUser(nonExistentId, updateData);
        });
    }

    @Test
    @WithMockUser(username = "user", roles = {"USER"})
    void testUpdateUser_modifiedByField() {
        User existingUser = userRepository.findByUsername("john.doe");
        LocalDateTime originalUpdatedDate = existingUser.getUpdatedAt();
        
        UserModel updateData = UserModel.fromEntity(existingUser);
        updateData.setDescription("Updated description");
        
        userService.updateUser(existingUser.getId(), updateData);
        
        User updatedUser = userRepository.findById(existingUser.getId()).orElseThrow();
        assertNotNull(updatedUser.getModifiedBy());
        assertNotEquals(originalUpdatedDate, updatedUser.getUpdatedAt());
        assertEquals("Updated description", updatedUser.getDescription());
    }

    @Test
    void testGetCurrentUserId() {
        assertEquals(100L, userService.getCurrentUserId());
    }

    @Test
    void testUsernameExists_validUser() {
        assertTrue(userService.usernameExists("john.doe"));
        assertFalse(userService.usernameExists("nonexistent.user"));
    }

    @Test
    void testUsernameExists_nullInput() {
        assertThrows(IllegalArgumentException.class, () -> {
            userService.usernameExists(null);
        });
    }

    @Test
    void testUsernameExists_emptyInput() {
        assertThrows(IllegalArgumentException.class, () -> {
            userService.usernameExists("");
        });
    }

    @Test
    void testEmailExists() {
        assertTrue(userService.emailExists("john@example.com"));
        assertFalse(userService.emailExists("nonexistent@example.com"));
    }

    @Test
    @WithMockUser(username = "test")
    void testDeleteUser_valid() {
        User existingUser = userRepository.findByUsername("john.doe");
        assertNotNull(existingUser);
        
        userService.deleteUser(existingUser.getId());
        
        assertFalse(userRepository.existsById(existingUser.getId()));
    }

    @Test
    void testDeleteUser_nonExistent() {
        assertThrows(NotFoundException.class, () -> {
            userService.deleteUser(-1L);
        });
    }

    @Test
    @WithMockUser(username = "test")
    void testCreateUser_transactionRollbackOnDuplicate() {
        UserModel newUser = new UserModel();
        newUser.setUsername("john.doe"); // Duplicate
        newUser.setEmail("unique@example.com");
        
        try {
            userService.createUser(newUser);
            fail("Expected ValidationException");
        } catch (ValidationException e) {
            // Verify no user was created
            assertEquals(3, userRepository.count());
        }
    }

    @Test
    @WithMockUser(username = "test")
    void testCreateUser_createdByField() {
        UserModel newUser = new UserModel();
        newUser.setUsername("new.user");
        newUser.setEmail("new@example.com");
        newUser.setProvider("keycloak");
        newUser.setProviderId(UUID.randomUUID().toString());
        newUser.setType(User.UserType.STAFF);
        
        UserModel created = userService.createUser(newUser);
        User dbUser = userRepository.findById(created.getId()).orElseThrow();
        
        assertEquals("joe", dbUser.getCreatedBy());
    }
}
