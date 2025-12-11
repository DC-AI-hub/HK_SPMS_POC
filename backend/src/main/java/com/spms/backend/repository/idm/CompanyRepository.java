package com.spms.backend.repository.idm;

import com.spms.backend.repository.entities.idm.CompanyType;
import com.spms.backend.repository.BaseRepository;
import java.util.List;
import com.spms.backend.repository.entities.idm.Company;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

@Repository
public interface CompanyRepository extends BaseRepository<Company, Long> {
    Page<Company> findByNameContainingIgnoreCase(String name, Pageable pageable);
    boolean existsByName(String name);
    Page<Company> findByParentId(Long parentId, Pageable pageable);
    Page<Company> findByParentIdAndNameContainingIgnoreCase(Long parentId, String name, Pageable pageable);
    
    List<Company> findByCompanyType(CompanyType type);
    Page<Company> findByCompanyType(CompanyType type, Pageable pageable);
    Page<Company> findByCompanyTypeAndNameContainingIgnoreCase(CompanyType type, String name, Pageable pageable);
    List<Company> findByParentIdAndCompanyType(Long parentId, CompanyType type);
}
