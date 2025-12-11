package com.spms.backend.repository.sys;

import com.spms.backend.repository.entities.sys.SystemConfigEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SystemConfigRepository extends JpaRepository<SystemConfigEntity, Long> {

    Optional<SystemConfigEntity> findByKey(String key);

    boolean existsByKey(String key);

    List<SystemConfigEntity> findByCategory(String category);

    List<SystemConfigEntity> findByCategoryOrderByKey(String category);

    List<SystemConfigEntity> findByDataType(String dataType);

    @Query("SELECT DISTINCT sc.category FROM SystemConfigEntity sc WHERE sc.category IS NOT NULL ORDER BY sc.category")
    List<String> findDistinctCategories();

    @Query("SELECT sc FROM SystemConfigEntity sc WHERE sc.key LIKE %:searchTerm% OR sc.description LIKE %:searchTerm%")
    List<SystemConfigEntity> searchByKeyOrDescription(@Param("searchTerm") String searchTerm);
}
