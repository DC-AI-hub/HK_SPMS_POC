package com.spms.backend.service.idm.impl;

import com.spms.backend.controller.dto.idm.ChartMode;
import com.spms.backend.controller.dto.idm.OrganizationChartDTO;
import com.spms.backend.service.exception.NotFoundException;
import com.spms.backend.repository.entities.idm.CompanyType;
import com.spms.backend.repository.entities.idm.DepartmentType;
import com.spms.backend.service.idm.*;
import com.spms.backend.service.model.idm.*;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.Random;
import java.util.Collections;
import java.util.ArrayList;

@Service("organizationService")
public class OrganizationServiceImpl implements OrganizationService {

    private final UserService userService;
    private final CompanyService companyService;
    private final DivisionService divisionService;
    private final DepartmentService departmentService;

    /**
     * Constructs an OrganizationServiceImpl with required services
     * 
     * @param divisionService service for division operations
     * @param departmentService service for department operations
     * @param companyService service for company operations
     * @param userService service for user operations
     */
    public OrganizationServiceImpl(
            DivisionService divisionService,
            DepartmentService departmentService,
            CompanyService companyService,
            UserService userService
    ) {
        this.divisionService = divisionService;
        this.departmentService = departmentService;
        this.companyService = companyService;
        this.userService = userService;
    }


    /**
     * Retrieves organization chart for a company in specified mode
     * 
     * @param companyId ID of the company
     * @param mode chart display mode (REALISTIC or FUNCTIONAL)
     * @return organization chart DTO
     */
    @Override
    public OrganizationChartDTO getOrganizationChart(Long companyId, ChartMode mode) {
        CompanyModel groupCompany = companyService.getCompanyByCompanyId(companyId)
                .orElseThrow(() -> new NotFoundException("Company not found"));

        if (mode == ChartMode.REALISTIC) {
            return buildRealisticChart(groupCompany);
        } else {
            return buildFunctionalChart(groupCompany);
        }
    }

    /**
     * Assigns roles to a user
     * 
     * @param userModel user to assign roles to
     * @param roles list of roles to assign
     * @return true if successful, false otherwise
     */
    @Override
    public boolean assignRoles(UserModel userModel, List<RoleModel> roles) {
        return userService.assignRoles(userModel, roles);
    }

    /**
     * Unassigns roles from a user
     * 
     * @param userModel user to unassign roles from
     * @param roles list of roles to unassign
     * @return true if successful, false otherwise
     */
    @Override
    public boolean unassignRoles(UserModel userModel, List<RoleModel> roles) {
        return userService.unassignRoles(userModel, roles);
    }

    /**
     * Builds realistic organization chart showing business structure
     * 
     * @param groupCompany top-level company model
     * @return organization chart DTO
     */
    private OrganizationChartDTO buildRealisticChart(CompanyModel groupCompany) {
        OrganizationChartDTO chart = new OrganizationChartDTO();
        chart.setId(groupCompany.getId().toString());
        chart.setName(groupCompany.getName());
        chart.setType("GROUP");

        // Get all business entities under this group
        List<CompanyModel> businessEntities = companyService.findByParentIdAndCompanyType(
                groupCompany.getId(), CompanyType.BUSINESS_ENTITY);

        businessEntities.forEach(entity -> {
            OrganizationChartDTO entityNode = new OrganizationChartDTO();
            entityNode.setId(entity.getId().toString());
            entityNode.setName(entity.getName());
            entityNode.setType("BUSINESS_ENTITY");

            // Get local departments under this business entity
            List<DepartmentModel> localDepartments = departmentService.findByParentAndType(
                    entity.getId().toString(), DepartmentType.LOCAL);

            addDepartmentNodes(entityNode, localDepartments, 1);

            chart.getChildren().add(entityNode);
        });

        List<CompanyModel> customer = companyService.findByParentIdAndCompanyType(
                groupCompany.getId(), CompanyType.CUSTOMER);
        customer.forEach(entity -> {
            OrganizationChartDTO entityNode = new OrganizationChartDTO();
            entityNode.setId(entity.getId().toString());
            entityNode.setName(entity.getName());
            entityNode.setType("CUSTOMER");
            chart.getChildren().add(entityNode);
        });


        List<CompanyModel> vendor = companyService.findByParentIdAndCompanyType(
                groupCompany.getId(), CompanyType.VENDOR);

        vendor.forEach(entity -> {
            OrganizationChartDTO entityNode = new OrganizationChartDTO();
            entityNode.setId(entity.getId().toString());
            entityNode.setName(entity.getName());
            entityNode.setType("VENDOR");
            chart.getChildren().add(entityNode);
        });
        return chart;
    }

    /**
     * Builds functional organization chart showing divisions
     * 
     * @param groupCompany top-level company model
     * @return organization chart DTO
     */
    private OrganizationChartDTO buildFunctionalChart(CompanyModel groupCompany) {
        OrganizationChartDTO chart = new OrganizationChartDTO();
        chart.setId(groupCompany.getId().toString());
        chart.setName(groupCompany.getName());
        chart.setType("GROUP");

        // Get all divisions under this group
        List<DivisionModel> divisions = divisionService.findByCompanyId(groupCompany.getId());

        divisions.forEach(division -> {
            OrganizationChartDTO divisionNode = new OrganizationChartDTO();
            divisionNode.setId(division.getId().toString());
            divisionNode.setName(division.getName());
            divisionNode.setType("DIVISION");

            // Get functional departments under this division
            List<DepartmentModel> functionalDepartments = departmentService.findByParentAndType(
                    division.getId().toString(), DepartmentType.FUNCTIONAL);

            addDepartmentNodes(divisionNode, functionalDepartments, 1);

            chart.getChildren().add(divisionNode);
        });
        return chart;
    }

    /**
     * Recursively adds department nodes to organization chart
     * 
     * @param parentNode parent node in chart
     * @param departments list of department models
     * @param currentLevel current recursion depth
     */
    private void addDepartmentNodes(OrganizationChartDTO parentNode, List<DepartmentModel> departments, int currentLevel) {
        if (currentLevel > 4) return; // Max hierarchy level

        departments.forEach(dept -> {
            OrganizationChartDTO deptNode = new OrganizationChartDTO();
            deptNode.setId(dept.getId().toString());
            deptNode.setName(dept.getName());
            deptNode.setType("DEPARTMENT");

            if (currentLevel < 4) {
                // Get child departments
                List<DepartmentModel> childDepartments = departmentService.findByParentAndType(
                        dept.getId().toString(), dept.getType());
                addDepartmentNodes(deptNode, childDepartments, currentLevel + 1);
            }
            parentNode.getChildren().add(deptNode);
        });
    }

    /**
     * Removes user from a department
     * 
     * @param userModel user to remove
     * @param department department to remove from
     * @return true if successful, false otherwise
     */
    public boolean leaveDepartment(UserModel userModel, DepartmentModel department) {
        return departmentService.addUserToDepartment(department, List.of(userModel));
    }

    /**
     * Adds user to a department
     * 
     * @param user user to add
     * @param department department to join
     * @return true if successful, false otherwise
     */
    public boolean joinDepartment(UserModel user, DepartmentModel department) {
        return departmentService.deleteUserFromDepartment(department, List.of(user));
    }

    /**
     * Adds multiple users to a department
     * 
     * @param department target department
     * @param users list of users to add
     * @return true if successful, false otherwise
     */
    public boolean addUsersFromDepartment(DepartmentModel department, List<UserModel> users) {
        return departmentService.addUserToDepartment(department, users);
    }

    /**
     * Deletes users from a department
     * 
     * @param department target department
     * @param users list of users to remove
     * @return true if successful, false otherwise
     */
    public boolean deleteUserFromDepartment(DepartmentModel department, List<UserModel> users) {
        return departmentService.deleteUserFromDepartment(department, users);
    }


    /**
     * Finds department head for Flowable UEL expressions
     * ${organizationService.findUserDepartmentHead(${initiator},"LOCAL")}
     * @param userName username to lookup
     * @param line department line type ("FUNCTIONAL" or "LOCAL")
     * @return department head username or empty string
     */
    @Override
    public String findUserDepartmentHead(String userName, String line) {
        UserModel user = userService.getUserByUserName(userName);
        if (user == null) {
            return "";
        }
        
        DepartmentModel department = null;
        if ("FUNCTIONAL".equalsIgnoreCase(line)) {
            department = user.getFunctionalDepartment();
        } else if ("LOCAL".equalsIgnoreCase(line)) {
            department = user.getLocalDepartment();
        }
        
        if (department == null || department.getDepartmentHead() == null) {
            return "";
        }
        return department.getDepartmentHead().getUsername();
    }



    private static final ConcurrentHashMap<String, AtomicInteger> sequenceIndexMap = new ConcurrentHashMap<>();

    /**
     * For flowable engine UEL Expression like:organizationService.findDepartmentMember('HR—Salary','random')
     * 
     * @param departmentId department ID to search
     * @param method selection method ("random" or "sequence")
     * @return selected member username or empty string
     */
    @Override
    public String findDepartmentMember(String departmentId, String method) {
        DepartmentModel department = departmentService.getDepartmentById(Long.parseLong(departmentId))
            .orElse(null);
        if (department == null) {
            return "";
        }
        
        List<UserModel> members = userService.getUserFilterByDepartment(department);
        if (members.isEmpty()) {
            return "";
        }
        
        if ("random".equalsIgnoreCase(method)) {
            int randomIndex = new Random().nextInt(members.size());
            return members.get(randomIndex).getUsername();
        } else if ("sequence".equalsIgnoreCase(method)) {
            AtomicInteger index = sequenceIndexMap.computeIfAbsent(
                departmentId, k -> new AtomicInteger(0));
            int currentIndex = index.getAndUpdate(i -> (i + 1) % members.size());
            return members.get(currentIndex).getUsername();
        }
        
        return "";
    }

    /**
     * For flowable engine UEL Expression like:organizationService.findDepartmentMember('ITD', 10 )
     * 
     * @param departmentId department ID to search
     * @param depth hierarchy depth to include
     * @return list of member usernames
     */
    @Override
    public List<String> findDepartmentMembers(String departmentId, int depth) {
        if (depth < 1) {
            return Collections.emptyList();
        }
        
        DepartmentModel department = departmentService.getDepartmentById(Long.parseLong(departmentId))
            .orElse(null);
        if (department == null) {
            return Collections.emptyList();
        }
        
        List<String> members = new ArrayList<>();
        // Get immediate members
        List<UserModel> immediateMembers = userService.getUserFilterByDepartment(department);
        immediateMembers.stream()
            .map(UserModel::getUsername)
            .forEach(members::add);
        
        // Recursively get child department members if depth > 1
        if (depth > 1) {
            List<DepartmentModel> childDepartments = departmentService.findByParentAndType(
                department.getId().toString(), department.getType());
            for (DepartmentModel child : childDepartments) {
                members.addAll(findDepartmentMembers(child.getId().toString(), depth - 1));
            }
        }
        
        return members;
    }

    /**
     * For flowable engine UEL Expression like:organizationService.findDepartmentMember('Application Engineering' )
     * 
     * @param departmentId department ID to search
     * @return list of member usernames
     */
    @Override
    public List<String> findDepartmentMembers(String departmentId) {
        return findDepartmentMembers(departmentId, 1);
    }

}
