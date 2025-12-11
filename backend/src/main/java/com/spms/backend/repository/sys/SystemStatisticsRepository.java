package com.spms.backend.repository.sys;

import com.spms.backend.repository.entities.sys.SystemStatistics;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

@Repository
public interface SystemStatisticsRepository extends JpaRepository<SystemStatistics, String> {
    List<SystemStatistics> findByAsOfDateBetween(Date startDate, Date endDate);
    
    List<SystemStatistics> findByNameOrderByAsOfDateDesc(String name);
}
