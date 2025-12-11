# Feature Specification: Timecard Module Refactoring

**Feature Branch**: `002-functional-specification-md`  
**Created**: 2025-01-22  
**Status**: Draft  
**Input**: User description: "请学习 @Functional Specification.md 的功能需求章节，所有的功能已确认，可以生成Spec了。"

## Clarifications

### Session 2025-01-22

- Q: Should the Form-Process-Association workflow (creating Timecard form, registering custom component, associating with process) be explicitly documented in the spec? → A: Yes, include as detailed user story and precondition. After Timecard form is created, users should be able to see it in Form Management, which is a precondition for process creation.
- Q: Should the form_data JSON structure be explicitly defined in the spec? → A: Yes, define in Key Entities. Must include all date entries, summary, and employee basic information. Add rendering logic: if Flowable engine has complete data, render from there; otherwise, fetch from backend API and auto-populate. This distinguishes between approver viewing form vs. employee filling form.
- Q: How is approval routing determined - automatically from organizational hierarchy or configured in BPMN? → A: Approval routing is configured in BPMN process definition through process editor. Managers are selected in BPMN, and system routes based on organizational hierarchy at runtime. This functionality is already implemented, no changes needed in timecard module.
- Q: How is monthly deadline determined - fixed rule, configurable parameter, or business rule? → A: Configurable system parameter stored in system_config table, allowing adjustment by month or organization.
- Q: What is the specific behavior of batch timecard entry (current week, all working days, or blank days)? → A: Copy data from a filled date as template: user selects a date that already has timecard entries, and system copies those entries to target date range (current week/all working days/blank days only).

## User Scenarios & Testing *(mandatory)*

### User Story 0 - Timecard Form Setup and Registration (Priority: P1)

As a system administrator, I need to create and register the Timecard form as a custom component so that it can be associated with BPMN processes for timecard submission and approval workflows.

**Why this priority**: This is a prerequisite for all timecard functionality. Without the form being properly registered and associated with processes, employees cannot submit timecards and managers cannot approve them. This must be completed before User Story 1 and 2 can function.

**Independent Test**: Can be fully tested by creating the Timecard custom form component, registering it in the custom component registry, creating a form version in Process-Form Management, and verifying the form appears in the form selection list when creating BPMN processes.

**Acceptance Scenarios**:

1. **Given** a system administrator has developed the Timecard custom form component, **When** they register it in the custom component registry (custom-registry.jsx), **Then** the Timecard form component is available for use in Process-Form Management
2. **Given** the Timecard form component is registered, **When** an administrator navigates to Process-Form Management, **Then** they can see the Timecard form listed in the available forms
3. **Given** an administrator views Process-Form Management, **When** they create a new form version and select the Timecard custom component, **Then** the form version is created with form_type='TIMECARD' and can be associated with BPMN processes
4. **Given** a BPMN process is being created, **When** the process creator selects a form for the process, **Then** the Timecard form version appears in the form selection list
5. **Given** a BPMN process is associated with the Timecard form, **When** a process instance is started, **Then** timecard data is stored in `spms_process_data` table with `form_type='TIMECARD'` and the form_data field contains the timecard entries in JSON format

**Preconditions**:
- Timecard custom form component must be developed and registered in the custom component registry
- Process-Form Management system must be operational
- Flowable BPMN engine must be available for process creation

---

### User Story 1 - Employee Timecard Entry (Priority: P1)

As an employee, I need to fill out my timecard with daily work hours so that my work time is accurately tracked and I can submit it for approval.

**Why this priority**: This is the core functionality of the timecard system. Without the ability to enter timecard data, no other features (approval, reporting) can function. This delivers immediate value to end users.

**Independent Test**: Can be fully tested by allowing an employee to log in, view their timecard calendar, add project entries with hours, and save/submit the timecard. This delivers the primary value of time tracking.

**Preconditions**:
- User Story 0 must be completed (Timecard form registered and associated with a BPMN process)
- Employee must have valid project codes available in the system
- Organizational data must be properly configured

**Acceptance Scenarios**:

1. **Given** an employee is logged in and no existing timecard data exists in Flowable, **When** they navigate to the timecard page, **Then** the system fetches their basic information from backend API and automatically populates the form (staff name in Chinese and English, team, staff ID, timecard month, staff type, department head, final approver)
2. **Given** an employee has an existing timecard draft in Flowable, **When** they navigate to the timecard page, **Then** the system loads the complete timecard data from Flowable process instance (including all entries, summary, and employee info) and renders it
3. **Given** an employee views the calendar, **When** they click on a date cell, **Then** a dialog opens allowing them to add project entries with fields: project code, task, activity, claim type, hours, and remark
4. **Given** an employee enters a valid project code, **When** they type or search for it, **Then** the system automatically fills in the task and activity fields
5. **Given** an employee enters hours exceeding 8 per day or fills hours on weekends, **When** they view the calendar, **Then** those date cells display with red background warning (but submission is still allowed)
6. **Given** an employee has entered timecard data, **When** they view the timecard summary, **Then** they see total hours grouped by Project Code + Task Number + Activity, and if total exceeds 160 hours, a yellow warning is displayed
7. **Given** an employee has filled out their timecard, **When** they click submit before the monthly deadline, **Then** the timecard is saved to Flowable process instance with complete JSON structure (employee_info, timecard_entries, summary) and can be resubmitted (latest version takes precedence)
8. **Given** an employee views the calendar, **When** they see a holiday date, **Then** that date cell displays with green background highlighting
9. **Given** an employee has filled out timecard entries for a specific date, **When** they select batch fill operation (current week/all working days/blank days) and choose that date as template, **Then** the system copies all entries from the template date to all target dates in the selected scope, without overwriting existing entries unless explicitly allowed

---

### User Story 2 - Timecard Approval Workflow (Priority: P1)

As a manager (local or functional), I need to review and approve employee timecards so that work hours are validated before final processing.

**Why this priority**: Approval is critical for data integrity and business process compliance. This must work in parallel with entry to complete the timecard lifecycle.

**Independent Test**: Can be fully tested by having a manager log in, view pending timecard approvals, see the same visual warnings (red/yellow backgrounds) as employees, approve or reject with comments, and have the system route to the next approver automatically.

**Acceptance Scenarios**:

1. **Given** a manager is logged in, **When** they navigate to approval tasks, **Then** they see timecards submitted by their direct reports, and the system renders complete timecard data from Flowable process instance (including all entries, summary, and employee info) with all visual warnings (red backgrounds for daily over 8 hours, yellow for monthly over 160 hours)
2. **Given** a timecard is submitted and a BPMN process instance is created, **When** the system processes the approval workflow, **Then** it routes to managers configured in the BPMN process definition (typically local manager first, then functional manager) based on organizational hierarchy lookup from spms_department table
3. **Given** a manager reviews a timecard, **When** they approve it, **Then** the timecard status updates and routes to the next approver (if applicable) or completes
4. **Given** a manager reviews a timecard, **When** they reject it with feedback, **Then** the timecard is returned to the employee with the feedback message, and the employee can resubmit
5. **Given** a timecard is submitted, **When** it requires manager attention, **Then** the manager receives email and system notifications

---

### User Story 3 - Organization Data Management (Priority: P2)

As a system administrator, I need to manage organizational data including department structure, manager assignments, and import/export capabilities so that the organizational hierarchy supports accurate timecard routing.

**Why this priority**: Organization data is foundational for approval routing and employee information display. While not directly user-facing, it's essential for system operation.

**Independent Test**: Can be fully tested by allowing an admin to import organization data from Excel, view/edit department information including local and functional manager assignments, export data, and verify that changes are reflected in timecard routing.

**Acceptance Scenarios**:

1. **Given** an administrator has Excel file with organization data, **When** they import it, **Then** the system validates and loads the four-level organizational structure
2. **Given** an administrator views organization data, **When** they edit a department, **Then** they can add or modify local manager and functional manager reporting lines
3. **Given** an administrator makes changes to organization data, **When** they save, **Then** the system records the operation in an audit log with operator, timestamp, and change details
4. **Given** an administrator needs to export organization data, **When** they click export, **Then** the system generates an Excel file with all current organization data

---

### User Story 4 - Project Management (Priority: P2)

As a project administrator, I need to manage project codes, tasks, and activities so that employees can accurately assign their time to the correct projects.

**Why this priority**: Projects are required for timecard entries. Employees need valid project codes to fill out timecards. This enables the core timecard entry functionality.

**Independent Test**: Can be fully tested by allowing an admin to create/edit projects with codes, names, tasks, and activities, set project status (active/completed), search by project code, and verify that only active projects appear in timecard project selection lists.

**Acceptance Scenarios**:

1. **Given** a project administrator views the project list, **When** they create a new project, **Then** they can enter project code (must be unique), project name, task number, activity, and set status (active/completed)
2. **Given** a project administrator searches for a project, **When** they enter a project code, **Then** the system returns matching project information
3. **Given** a project is marked as "completed", **When** an employee tries to select a project for timecard entry, **Then** that project does not appear in the selection list (only active projects are shown)
4. **Given** a project administrator has project data in Excel, **When** they import it, **Then** the system validates project codes for uniqueness and loads the data
5. **Given** a project administrator needs to export project data, **When** they click export, **Then** the system generates an Excel file with all project information

---

### User Story 5 - Holiday Management (Priority: P2)

As a system administrator, I need to configure holidays for different countries so that the timecard calendar correctly highlights holidays and employees can see when holidays occur.

**Why this priority**: Holiday highlighting improves user experience and helps employees understand why certain dates are marked differently. It's visible but not critical for core functionality.

**Independent Test**: Can be fully tested by allowing an admin to view the holiday calendar, add holidays for specific dates and countries, import/export holiday data via Excel, and verify that holidays appear with green background in employee timecard calendars.

**Acceptance Scenarios**:

1. **Given** an administrator views the holiday management calendar, **When** they click on a date, **Then** they can add a holiday with country, name, and type
2. **Given** an administrator adds a holiday, **When** they save it, **Then** that date displays with green background in both holiday management and employee timecard calendars
3. **Given** an administrator has holiday data in Excel, **When** they import it, **Then** the system loads holidays for multiple countries
4. **Given** an administrator needs to export holiday data, **When** they click export, **Then** the system generates an Excel file with all holiday information

---

### User Story 6 - Timecard Reports Generation (Priority: P3)

As a manager or administrator, I need to generate various timecard reports with filtering capabilities so that I can analyze work hours by project, department, time period, and employee type.

**Why this priority**: Reporting provides business value for analysis and decision-making but is not required for the core timecard entry and approval workflow. This can be delivered after core functionality.

**Independent Test**: Can be fully tested by allowing a manager/admin to select report type, apply filters (department, project, time period, staff type), generate reports, and export to Excel. Reports should show accurate aggregated data based on approved timecards.

**Acceptance Scenarios**:

1. **Given** a manager views the reports page, **When** they select "Timecard Summary" report, **Then** they can filter by project, department, and time period, and see aggregated hours
2. **Given** an employee views reports, **When** they select "Individual Timecard Record", **Then** they see only their own timecard records
3. **Given** an administrator views reports, **When** they select "Individual Timecard Record", **Then** they see all employees' timecard records
4. **Given** a manager generates a report, **When** they apply filters and click export, **Then** the system generates an Excel file with the filtered report data matching the defined report headers
5. **Given** a manager views "Perm Staff IT Record" report, **When** they generate it, **Then** the report shows only overtime (OT) data for permanent staff or vendor staff

---

### Edge Cases

- What happens when an employee tries to submit a timecard after the monthly deadline? (System should validate against configurable deadline in system_config and prevent submission with appropriate error message)
- How does the system handle duplicate project code entries during import?
- What happens when a manager in the approval chain is unavailable or deleted from the system?
- How does the system handle timecard entries where the project code is entered but doesn't exist in the project table?
- What happens when an employee tries to enter negative hours or hours exceeding 24 per day?
- How does the system handle timecard submission when the total hours exceed 160 but the employee still wants to submit?
- What happens when multiple employees submit timecards simultaneously for the same manager to approve?
- How does the system handle holiday data conflicts (same date, different countries)?
- What happens when a project status changes from active to completed while employees have pending timecard entries for that project?
- How does the system handle timecard data when an employee's department or manager assignment changes mid-month?

## Requirements *(mandatory)*

### Functional Requirements

#### Organization Data Maintenance Module

- **FR-001**: System MUST support migration of seed data from SPMS, including the four-level organizational structure
- **FR-002**: System MUST support CRUD operations on organizational data, maintaining functional logic consistent with SPMS
- **FR-003**: System MUST support adding, editing, and viewing local manager and functional manager reporting line fields
- **FR-004**: System MUST support importing and exporting organizational data in Excel format
- **FR-005**: System MUST record audit logs for all organizational data operations, including operator, timestamp, and change details

#### Timecard Form Setup and Registration Module

- **FR-000**: System MUST support creating Timecard as a custom form component that can be registered in the custom component registry
- **FR-000-1**: System MUST allow the Timecard form to be visible in Process-Form Management after registration
- **FR-000-2**: System MUST support creating form versions in Process-Form Management that reference the Timecard custom component
- **FR-000-3**: System MUST allow BPMN process definitions to select and associate with Timecard form versions
- **FR-000-4**: System MUST store timecard data in `spms_process_data` table with `form_type='TIMECARD'` when process instances are created
- **FR-000-5**: System MUST store complete timecard data in `form_data` JSON field including: employee_info (all basic information), timecard_entries (all date entries), and summary (grouped totals and warnings)

#### Timecard Entry Module

- **FR-006**: System MUST display employee basic information including staff name (Chinese), staff name (English), team, staff ID, timecard month, staff type, department head, and final approver
- **FR-007**: System MUST implement data rendering logic: if Flowable engine contains complete timecard data in process instance, render from Flowable data; otherwise, fetch employee basic information from backend API and auto-populate. This logic MUST distinguish between approver viewing submitted form (read from Flowable) vs. employee creating new form (fetch from API)
- **FR-007-1**: System MUST automatically populate employee basic information from backend API when no existing data exists in Flowable process instance
- **FR-008**: System MUST provide a Calendar View interface, defaulting to monthly view for displaying timecard data
- **FR-009**: System MUST use different colors in Calendar View to distinguish between different projects
- **FR-010**: System MUST display red background warning for dates where daily hours exceed 8 hours or where hours are entered on weekends, but MUST still allow submission
- **FR-011**: System MUST support timecard summary functionality that calculates total hours in real-time, grouped by Project Code + Task Number + Activity dimensions
- **FR-012**: System MUST display yellow warning when total hours exceed 160 hours (20 working days × 8 hours), but MUST still allow submission
- **FR-013**: System MUST display holiday dates with green background highlighting in Calendar View
- **FR-014**: System MUST support clicking on a calendar cell to open a dialog showing project entry list with fields: project code, task, activity, claim type, hours, and remark
- **FR-015**: System MUST support manual entry of project code, validate it exists in project table, and automatically populate task and activity fields if valid
- **FR-016**: System MUST support searching for project code and automatically populate task and activity fields upon selection
- **FR-017**: System MUST support claim type dropdown selection with options: normal, leave, OT (working days OT, holidays OT, statutory holidays OT)
- **FR-018**: System MUST restrict hours field input to prevent negative numbers or non-numeric strings
- **FR-019**: System MUST ensure all required fields are filled and display error messages when fields are missing
- **FR-020**: System MUST support batch timecard entry operations for current week, all working days, or blank days
- **FR-020-1**: System MUST allow users to select a date with existing timecard entries as a template and copy all entries from that date to target date range (current week, all working days in month, or only blank days)
- **FR-020-2**: System MUST only copy entries to dates that match the selected scope (current week, all working days, or blank days only) and must not overwrite existing entries unless explicitly allowed
- **FR-021**: System MUST provide "preview month adjust" button to modify previous month's timecard content, with fields: project code, task number, date, activity, hours (only for previous month)
- **FR-022**: System MUST implement project code auto-suggestion and validation functionality
- **FR-023**: System MUST validate hours input range to ensure compliance with business rules
- **FR-024**: System MUST support resubmission of timecard before monthly deadline, with latest submission taking precedence
- **FR-024-1**: System MUST validate timecard submission against configurable monthly deadline stored in system_config table (deadline can be configured per month or organization)
- **FR-024-2**: System MUST prevent timecard submission after the configured monthly deadline has passed
- **FR-025**: System MUST provide timecard status tracking, displaying current status (draft, submitted, approval pending, approved)

#### Timecard Approval Module

- **FR-026**: System MUST allow approvers to view employee submitted timecard information, including red background warnings for daily over 8 hours and yellow background warnings for monthly over 160 hours
- **FR-027**: System MUST support approval routing through BPMN process definition configuration (approval paths are configured in BPMN process editor, where managers can be selected based on organizational hierarchy)
- **FR-028**: System MUST route approval workflow based on organizational hierarchy at runtime when process instance is created (system looks up local_manager_id and functional_manager_id from spms_department table and assigns tasks accordingly)
- **FR-029**: System MUST support approval rejection with feedback comments for employees to address
- **FR-030**: System MUST automatically send approval reminder emails and system notifications

**Note**: Approval routing configuration is handled through BPMN process definition editor (already implemented). Timecard module only needs to submit data to trigger the process instance, and the system automatically routes to configured managers based on organizational hierarchy.

#### Holiday Management Module

- **FR-031**: System MUST provide Calendar View management interface (same as Timecard) for holiday configuration
- **FR-032**: System MUST support multi-country holiday settings
- **FR-033**: System MUST display holidays with green background highlighting in Timecard Calendar View
- **FR-034**: System MUST support importing holiday data in Excel format
- **FR-035**: System MUST support exporting holiday data in Excel format

#### Project Management Module

- **FR-036**: System MUST provide project list management page
- **FR-037**: System MUST support importing project content (project name, category, code) in Excel format
- **FR-038**: System MUST support exporting project content (project name, category, code) in Excel format
- **FR-039**: System MUST validate project code uniqueness to prevent duplicate creation
- **FR-040**: System MUST support CRUD operations on projects (create, modify; deletion not allowed), with status field ("active"/"completed"), where only active projects appear in timecard project selection lists
- **FR-041**: System MUST support searching project information by project code

#### Reports Module

- **FR-042**: System MUST generate Timecard Summary report, aggregating timecard data by project, department, and time period
- **FR-043**: System MUST generate Individual Timecard Record report with detailed employee timecard records, where employees can only view their own reports and administrators can view all employee reports
- **FR-044**: System MUST generate Timecard Details Report providing detailed timecard entries and financial details
- **FR-045**: System MUST generate Perm Staff IT Record report extracting OT data for permanent staff or vendor staff only
- **FR-046**: System MUST support searching and filtering each report by specific fields

### Key Entities *(include if feature involves data)*

- **Timecard Form Component**: Represents the custom React component for timecard entry, registered in the custom component registry and available for association with BPMN processes through Process-Form Management.

- **Timecard Entry**: Represents a single time entry for an employee on a specific date, containing project code, task number, activity, claim type, hours, and optional remark. Multiple entries can exist per day. Stored as JSON in `spms_process_data.form_data`.

- **Timecard**: Represents a complete monthly timecard submission for an employee, containing all timecard entries for that month, summary totals, status (draft/submitted/approved/rejected), submission timestamp, and approval history. Stored in `spms_process_data` table with `form_type='TIMECARD'`.

**Timecard form_data JSON Structure** (stored in `spms_process_data.form_data`):
```json
{
  "employee_info": {
    "staff_id": "EMP001",
    "staff_name_chinese": "张伟",
    "staff_name_english": "Wei Zhang",
    "team": "IT Development",
    "timecard_month": "2025-01",
    "staff_type": "PERMANENT",
    "department_head": "John Smith",
    "final_approver": "Sarah Johnson"
  },
  "timecard_entries": [
    {
      "date": "2025-01-15",
      "project_code": "PROJ001",
      "task_number": "T001",
      "activity": "开发",
      "claim_type": "NORMAL",
      "hours": 8.0,
      "remark": "日常开发工作"
    }
  ],
  "summary": {
    "total_hours": 160,
    "grouped_by_project_task_activity": [
      {
        "project_code": "PROJ001",
        "task_number": "T001",
        "activity": "开发",
        "total_hours": 120
      }
    ],
    "warning_flags": ["EXCEED_DAILY_LIMIT", "EXCEED_MONTHLY_LIMIT"]
  }
}
```

- **Project**: Represents a work project with unique project code, project name, associated tasks and activities, and status (active/completed). Only active projects are available for timecard entry.

- **Organization Structure**: Represents the four-level organizational hierarchy (Company → Division → Department → Team) with department head, local manager, and functional manager assignments that determine approval routing.

- **Holiday**: Represents a holiday date for a specific country, with date, country code, holiday name, and type (public/company holiday), used to highlight dates in the calendar.

- **Approval Workflow**: Represents the approval process for a timecard, with configurable routing path, current approver, approval status, and comments/feedback at each stage.

- **Report**: Represents generated timecard reports with specific data fields, filtering criteria, and export capabilities, supporting multiple report types (Summary, Individual Record, Details, OT Record).

## Success Criteria *(mandatory)*

### Measurable Outcomes

- **SC-001**: Employees can complete timecard entry for a full month (20 working days) in under 15 minutes from login to submission
- **SC-002**: System processes and routes timecard approvals to the correct managers within 30 seconds of submission
- **SC-003**: 95% of timecard submissions pass validation on first attempt (no rejection due to data errors)
- **SC-004**: Managers can review and approve/reject a timecard in under 2 minutes
- **SC-005**: System supports 500 concurrent users entering timecards simultaneously without performance degradation
- **SC-006**: Report generation completes within 10 seconds for datasets up to 10,000 timecard entries
- **SC-007**: Excel import/export operations complete successfully for files up to 5,000 records within 30 seconds
- **SC-008**: 90% of employees successfully find and select correct project codes using the search/autocomplete functionality
- **SC-009**: System maintains 99.9% data accuracy in approval routing based on organizational hierarchy
- **SC-010**: All visual warnings (red for daily over 8 hours, yellow for monthly over 160 hours, green for holidays) display correctly for 100% of applicable dates

## Assumptions

- Employees have access to valid project codes and understand the project/task/activity structure
- Organizational hierarchy is properly maintained and managers are correctly assigned
- Monthly timecard submission deadlines are configured in system_config table and can be adjusted per month or organization
- System integrates with existing SPMS user authentication and authorization
- Email notification system is available for sending approval reminders
- Excel file formats for import/export follow predefined templates
- Users have basic familiarity with calendar-based interfaces
- Network connectivity is available for real-time data validation and auto-population features
- Timecard form component is created and registered in custom component registry before timecard entry functionality is used
- Process-Form Management system is operational and allows form version creation with custom components
- Flowable BPMN engine is available for process definition and instance management
- BPMN process editor supports configuring approval paths and selecting managers based on organizational hierarchy (this functionality is already implemented)
- Test accounts: spms-admin (employee role) and spms-appe-head (approver role) are available for testing
- Approval routing is configured in BPMN process definitions, not in timecard form itself

