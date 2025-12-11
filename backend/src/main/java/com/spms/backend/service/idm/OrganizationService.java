package com.spms.backend.service.idm;

import com.spms.backend.controller.dto.idm.ChartMode;
import com.spms.backend.controller.dto.idm.OrganizationChartDTO;
import com.spms.backend.service.model.idm.DepartmentModel;
import com.spms.backend.service.model.idm.RoleModel;
import com.spms.backend.service.model.idm.UserModel;

import java.util.List;

/**
 * Service interface for organization management operations including
 * organization chart retrieval, role assignments, and department management.
 */
public interface OrganizationService {

    /**
     * Retrieves the organization chart for a specified company.
     *
     * @param companyId The ID of the company
     * @param mode The display mode of the chart (e.g., simplified, detailed)
     * @return OrganizationChartDTO containing chart data
     */
    OrganizationChartDTO getOrganizationChart(Long companyId, ChartMode mode);

    /**
     * Assigns roles to a user.
     *
     * @param userModel The user to assign roles to
     * @param roles List of roles to assign
     * @return true if assignment was successful, false otherwise
     */
    boolean assignRoles(UserModel userModel, List<RoleModel> roles);

    /**
     * Removes roles from a user.
     *
     * @param userModel The user to remove roles from
     * @param roles List of roles to remove
     * @return true if removal was successful, false otherwise
     */
    boolean unassignRoles(UserModel userModel, List<RoleModel> roles);

    /**
     * Removes a user from a department.
     *
     * @param userId The user to remove
     * @param companyId The department to remove from
     * @return true if removal was successful, false otherwise
     */
    boolean leaveDepartment(UserModel userId, DepartmentModel companyId);

    /**
     * Adds a user to a department.
     *
     * @param userId The user to add
     * @param companyId The department to add to
     * @return true if addition was successful, false otherwise
     */
    boolean joinDepartment(UserModel userId, DepartmentModel companyId);

    /**
     * Adds multiple users to a department.
     *
     * @param department The target department
     * @param users List of users to add
     * @return true if operation was successful, false otherwise
     */
    boolean addUsersFromDepartment(DepartmentModel department, List<UserModel> users);

    /**
     * Removes multiple users from a department.
     *
     * @param department The target department
     * @param users List of users to remove
     * @return true if operation was successful, false otherwise
     */
    boolean deleteUserFromDepartment(DepartmentModel department, List<UserModel> users);

    /**
     * Finds the department head for a user in a specific line.
     * Used in Flowable engine UEL expressions.
     *
     * @param userName The username to look up
     * @param line The functional line (e.g., "local" for direct line)
     * @return Department head identifier
     */
    String findUserDepartmentHead(String userName, String line);

    /**
     * Finds a department member using a specific selection method.
     *
     * @param departmentId The department ID
     * @param method The selection method (e.g., "random", "first")
     * @return Selected department member identifier
     */
    String findDepartmentMember(String departmentId, String method);

    /**
     * Retrieves all members of a department.
     *
     * @param departmentId The department ID
     * @return List of department member identifiers
     */
    List<String> findDepartmentMembers(String departmentId);

    /**
     * Retrieves department members including sub-departments to a specified depth.
     *
     * @param departmentId The root department ID
     * @param depth The hierarchy depth to include (0 = current only)
     * @return List of department member identifiers
     */
    List<String> findDepartmentMembers(String departmentId, int depth);

}
