package com.spms.backend.service;

import com.spms.backend.repository.entities.sys.UserActivity;
import com.spms.backend.repository.UserActivityRepository;
import com.spms.backend.service.model.UserActivityModel;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Service for tracking and retrieving user activities
 */
@Service
public class ActivityLogService {
    private final UserActivityRepository userActivityRepository;

    public ActivityLogService(UserActivityRepository userActivityRepository) {
        this.userActivityRepository = userActivityRepository;
    }

    /**
     * Logs a user activity using the provided model
     * @param activityModel The activity model containing user activity details
     */
    @Transactional
    public void logActivity(UserActivityModel activityModel) {
        UserActivity activity = activityModel.toEntityForCreate();
        activity.setCreatedAt(new Date().getTime());
        userActivityRepository.save(activity);
    }

    /**
     * Retrieves recent user activities and converts to models
     * @return List of UserActivityModel for recent activities
     */
    public List<UserActivityModel> getRecentActivities() {
        return userActivityRepository.findTop10ByOrderByCreatedAtDesc()
            .stream()
            .map(ActivityLogService::convertToModel)
            .collect(Collectors.toList());
    }

    /**
     * Converts a UserActivity entity to a UserActivityModel
     * @param activity The entity to convert
     * @return The converted model
     */
    private static UserActivityModel convertToModel(UserActivity activity) {
        return UserActivityModel.fromEntity(activity);
    }
}
