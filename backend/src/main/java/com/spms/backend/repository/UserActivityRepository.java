package com.spms.backend.repository;

import com.spms.backend.repository.entities.sys.UserActivity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface UserActivityRepository extends JpaRepository<UserActivity, Long> {

    @Query("SELECT COUNT(ua) FROM UserActivity ua WHERE ua.createdAt >= :since")
    long countActivitiesSince(LocalDateTime since);

    @Query("SELECT COUNT(DISTINCT ua.userId) FROM UserActivity ua WHERE ua.createdAt >= :since")
    long countActiveUsersSince(LocalDateTime since);

    List<UserActivity> findTop10ByOrderByCreatedAtDesc();
}
