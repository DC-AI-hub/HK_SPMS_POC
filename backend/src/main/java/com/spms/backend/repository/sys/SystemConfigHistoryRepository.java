package com.spms.backend.repository.sys;

import com.spms.backend.repository.entities.sys.SystemConfigHistoryEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SystemConfigHistoryRepository extends JpaRepository<SystemConfigHistoryEntity, Long> {

    List<SystemConfigHistoryEntity> findByConfigIdOrderByChangeTimestampDesc(Long configId);

    Page<SystemConfigHistoryEntity> findByConfigIdOrderByChangeTimestampDesc(Long configId, Pageable pageable);

    List<SystemConfigHistoryEntity> findByChangedByOrderByChangeTimestampDesc(String changedBy);

    @Query("SELECT h FROM SystemConfigHistoryEntity h WHERE h.config.key = :configKey ORDER BY h.changeTimestamp DESC")
    List<SystemConfigHistoryEntity> findByConfigKeyOrderByChangeTimestampDesc(@Param("configKey") String configKey);

    @Query("SELECT h FROM SystemConfigHistoryEntity h WHERE h.config.key = :configKey ORDER BY h.changeTimestamp DESC")
    Page<SystemConfigHistoryEntity> findByConfigKeyOrderByChangeTimestampDesc(@Param("configKey") String configKey, Pageable pageable);

    @Query("SELECT h FROM SystemConfigHistoryEntity h WHERE h.changeType = :changeType ORDER BY h.changeTimestamp DESC")
    List<SystemConfigHistoryEntity> findByChangeTypeOrderByChangeTimestampDesc(@Param("changeType") String changeType);
}
