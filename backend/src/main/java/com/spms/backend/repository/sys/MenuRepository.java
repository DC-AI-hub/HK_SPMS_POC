package com.spms.backend.repository.sys;

import com.spms.backend.repository.entities.sys.MenuEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@Repository
public interface MenuRepository extends JpaRepository<MenuEntity, Long> {

    Optional<MenuEntity> findByTitle(String title);

    Optional<MenuEntity> findByPath(String path);

    boolean existsByTitle(String title);

    boolean existsByPath(String path);

    boolean existsByTitleAndPath(String title, String path);

    List<MenuEntity> findByActiveTrueOrderByDisplayOrderAsc();

    List<MenuEntity> findByParentMenuIsNullOrderByDisplayOrderAsc();

    List<MenuEntity> findByParentMenuIdOrderByDisplayOrderAsc(Long parentMenuId);

    @Query("SELECT m FROM MenuEntity m JOIN m.allowedRoles r WHERE r.id IN :roleIds AND m.active = true ORDER BY m.displayOrder ASC")
    List<MenuEntity> findByAllowedRolesIdInAndActiveTrue(@Param("roleIds") Set<Long> roleIds);

    @Query("SELECT m FROM MenuEntity m WHERE m.active = true AND (m.parentMenu IS NULL OR m.parentMenu.active = true) ORDER BY m.displayOrder ASC")
    List<MenuEntity> findActiveMenusWithActiveParents();

    Page<MenuEntity> findAllByActive(boolean active, Pageable pageable);

    @Query("SELECT COUNT(m) > 0 FROM MenuEntity m WHERE m.title = :title AND m.id != :excludeId")
    boolean existsByTitleAndIdNot(@Param("title") String title, @Param("excludeId") Long excludeId);

    @Query("SELECT COUNT(m) > 0 FROM MenuEntity m WHERE m.path = :path AND m.id != :excludeId")
    boolean existsByPathAndIdNot(@Param("path") String path, @Param("excludeId") Long excludeId);
}
