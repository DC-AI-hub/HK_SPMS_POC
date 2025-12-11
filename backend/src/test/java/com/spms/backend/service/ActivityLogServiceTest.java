package com.spms.backend.service;

import com.spms.backend.repository.entities.sys.UserActivity;
import com.spms.backend.repository.UserActivityRepository;
import com.spms.backend.service.model.UserActivityModel;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class ActivityLogServiceTest {
    @Mock
    private UserActivityRepository userActivityRepository;

    @InjectMocks
    private ActivityLogService activityLogService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void logActivity_SavesActivity() {
        // Create test model
        UserActivityModel model = new UserActivityModel();
        model.setUserId(1L);
        model.setActionType("CREATE");
        model.setEntityType("USER");
        model.setEntityId(2L);
        
        // Execute
        activityLogService.logActivity(model);
        
        // Verify
        verify(userActivityRepository, times(1)).save(any(UserActivity.class));
    }

    @Test
    void getRecentActivities_ReturnsConvertedModels() {
        // Setup test entity
        UserActivity entity = new UserActivity();
        entity.setId(1L);
        entity.setUserId(1L);
        entity.setActionType("CREATE");
        entity.setEntityType("USER");
        entity.setEntityId(2L);
        entity.setCreatedAt(System.currentTimeMillis());
        
        // Mock repository
        when(userActivityRepository.findTop10ByOrderByCreatedAtDesc())
            .thenReturn(Collections.singletonList(entity));
            
        // Execute
        List<UserActivityModel> activities = activityLogService.getRecentActivities();
        
        // Verify
        assertEquals(1, activities.size());
        UserActivityModel model = activities.get(0);
        assertNotNull(model);
        assertEquals(1L, model.getId());
        assertEquals("CREATE", model.getActionType());
    }
}
