package com.spms.backend.repository.idm;


import com.spms.backend.repository.entities.idm.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {
    Optional<Role> findByName(String name);
    boolean existsByName(String name);
    
    @Query("SELECT DISTINCT r FROM Role r " +
           "WHERE lower(r.name) LIKE lower(concat('%', :name, '%')) " +
           "AND lower(r.description) LIKE lower(concat('%', :description, '%'))")
    Page<Role> findByNameContainingIgnoreCaseAndDescriptionContainingIgnoreCase(
        @Param("name") String name, 
        @Param("description") String description, 
        Pageable pageable);
        
    @Query("SELECT r FROM Role r LEFT JOIN FETCH r.parentRoles LEFT JOIN FETCH r.childRoles LEFT JOIN FETCH r.permissions WHERE r.id = :id")
    Optional<Role> findByIdWithRelations(@Param("id") Long id);
    
    @Query("SELECT COUNT(r) > 0 FROM Role r JOIN r.parentRoles p WHERE r.id = :childId AND p.id = :parentId")
    boolean existsByChildIdAndParentId(@Param("childId") Long childId, @Param("parentId") Long parentId);
}
