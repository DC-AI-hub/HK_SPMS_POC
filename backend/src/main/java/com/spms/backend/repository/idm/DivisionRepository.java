package com.spms.backend.repository.idm;

import com.spms.backend.repository.entities.idm.Division;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DivisionRepository extends JpaRepository<Division, Long> {
    boolean existsByName(String name);
    Page<Division> findByNameContainingIgnoreCase(String name, Pageable pageable);
    Page<Division> findByCompanyId(Long companyId, Pageable pageable);
    Page<Division> findByCompanyIdAndNameContainingIgnoreCase(Long companyId, String name, Pageable pageable);
    List<Division> findByCompanyId(Long companyId);
}
