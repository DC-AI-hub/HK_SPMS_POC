package com.spms.backend.controller.idm;

import com.spms.backend.controller.dto.idm.CompanyDTO;
import com.spms.backend.controller.dto.idm.CreateCompanyRequestDTO;
import com.spms.backend.controller.dto.idm.OrganizationChartDTO;
import com.spms.backend.controller.dto.idm.ChartMode;
import com.spms.backend.repository.entities.idm.CompanyType;
import com.spms.backend.controller.dto.idm.JoinToChildrenRequestDTO;
import com.spms.backend.service.idm.CompanyService;
import com.spms.backend.service.idm.DivisionService;
import com.spms.backend.service.idm.OrganizationService;
import com.spms.backend.service.model.idm.CompanyModel;
import jakarta.transaction.Transactional;
import jakarta.websocket.server.PathParam;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/v1/companies")
public class CompanyController {

    private final CompanyService companyService;
    private final OrganizationService organizationService;

    protected CompanyController(CompanyService companyService,
                                DivisionService divisionService,
                                OrganizationService organizationService
    ) {
        this.companyService = companyService;
        this.organizationService = organizationService;
    }

    @Transactional
    @PostMapping("/{id}/children")
    public ResponseEntity<Page<CompanyDTO>> addChildren(
            @PathParam("id") Long companyId,
            JoinToChildrenRequestDTO requestDTO) {

        List<String> error = companyService.canAddAsChildren(companyId, requestDTO.getIds());
        // TODO: explain the ERROR and response.
        if(!error.isEmpty()){
            return  ResponseEntity.badRequest().build();
        }

        companyService.addCompanyToChildren(companyId, requestDTO.getIds());
        return getChildren(companyId, null, Pageable.ofSize(Integer.MAX_VALUE));
    }

    @GetMapping("/{id}/children")
    public ResponseEntity<Page<CompanyDTO>> getChildren(
            @PathParam("id") Long companyId,
            @RequestParam(required = false) String search,
            Pageable pageable
    ) {
        if (!companyService.isCompanyExists(companyId)) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(companyService.getChildren(companyId, search, pageable).map(CompanyDTO::fromCompanyModel));
    }

    @GetMapping
    public ResponseEntity<Page<CompanyDTO>> getAllCompanies(
            @RequestParam(required = false) String search,
            @RequestParam(required = false) CompanyType type,
            Pageable pageable) {

        return ResponseEntity.ok(companyService.getAllCompanies(search, type, pageable)
                .map(CompanyDTO::fromCompanyModel)
        );
    }


    @GetMapping("/valid-parents")
    public ResponseEntity<List<CompanyDTO>> getValidParents(
            @RequestParam CompanyType type) {
        return ResponseEntity.ok(companyService.getValidParents(type)
                .stream()
                .map(CompanyDTO::fromCompanyModel)
                .toList());
    }

    @PostMapping
    public ResponseEntity<CompanyDTO> createCompany(@RequestBody CreateCompanyRequestDTO companyDTO) {
        if (!companyService.isValidParent(companyDTO.getCompanyType(), companyDTO.getParentId())) {
            return ResponseEntity.badRequest().build();
        }
        if (companyDTO.getCompanyType() == CompanyType.GROUP) {
            //Group is a kind of top withoout parent
            return Optional.ofNullable(companyService.createCompany(companyDTO.toCompanyModel()))
                    .map(x -> ResponseEntity.ok(CompanyDTO.fromCompanyModel(x)))
                    .orElse(ResponseEntity.notFound().build());
        } else {
            Optional<CompanyModel> company = companyService.getCompanyByCompanyId(companyDTO.getParentId());

            return company.map(companyModel -> ResponseEntity.ok(CompanyDTO.fromCompanyModel(
                            companyService.createCompany(companyDTO.toCompanyModel(companyModel)))))
                    .orElseGet(() -> ResponseEntity.notFound().build());
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<CompanyDTO> updateCompany(
            @PathVariable Long id,
            @RequestBody CompanyDTO companyDTO) {
        return ResponseEntity.ok(CompanyDTO.fromCompanyModel(
                companyService.updateCompany(id, companyDTO.toCompanyModel())));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCompany(@PathVariable Long id) {
        companyService.deleteCompany(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}/organization-chart")
    public ResponseEntity<OrganizationChartDTO> getOrganizationChart(
            @PathVariable Long id,
            @RequestParam ChartMode mode) {
        return ResponseEntity.ok(organizationService.getOrganizationChart(id, mode));
    }
}
