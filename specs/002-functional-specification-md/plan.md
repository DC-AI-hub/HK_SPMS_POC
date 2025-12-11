# Implementation Plan: Timecard Module Refactoring

**Branch**: `002-functional-specification-md` | **Date**: 2025-01-22 | **Spec**: [spec.md](./spec.md)
**Input**: Feature specification from `/specs/002-functional-specification-md/spec.md`

## Summary

This feature implements a comprehensive Timecard module for the SPMS system from scratch, enabling employees to track work hours, managers to approve timecards, and administrators to manage projects, holidays, and generate reports. The implementation will leverage existing SPMS infrastructure including Flowable BPMN engine, Process-Form Management system, and organizational hierarchy. Timecard data will be stored in the existing `spms_process_data` table with `form_type='TIMECARD'`, and the Timecard form will be implemented as a custom React component registered in the custom component registry.

**Technical Approach**: 
- Frontend: React 18.3.1 with Material-UI (MUI) v5, custom Timecard form component integrated with Process-Form Management
- Backend: Spring Boot 3.4.5 with Spring Data JPA, RESTful APIs for timecard operations, integration with Flowable BPMN engine
- Database: PostgreSQL with existing `spms_process_data` table for timecard storage, `system_config` for holiday and deadline configuration
- Integration: Custom form component registration, BPMN process association, organizational hierarchy-based approval routing

## Technical Context

**Language/Version**: Java 17+ (Spring Boot 3.4.5), JavaScript/TypeScript (React 18.3.1)  
**Primary Dependencies**: 
- Backend: Spring Boot 3.4.5, Spring Data JPA, Flowable 7.1.0, Spring Security OAuth2, PostgreSQL Driver
- Frontend: React 18.3.1, Material-UI v5, react-i18next, React Router v6  
**Storage**: PostgreSQL 13+ with existing tables: `spms_process_data`, `spms_user`, `spms_department`, `spms_project`, `spms_task`, `system_config`  
**Testing**: JUnit 5 (backend), Jest/React Testing Library (frontend), Flowable process testing  
**Target Platform**: Web application (browser-based), Linux server deployment  
**Project Type**: Web application (frontend + backend)  
**Performance Goals**: 
- Support 500 concurrent users entering timecards (SC-005)
- Process approval routing within 30 seconds (SC-002)
- Report generation within 10 seconds for 10,000 entries (SC-006)
- Excel import/export within 30 seconds for 5,000 records (SC-007)  
**Constraints**: 
- Must integrate with existing Flowable BPMN engine (no modifications to Flowable)
- Must use existing Process-Form Management system
- Must follow existing SPMS database schema and naming conventions
- Must maintain compatibility with existing organizational hierarchy structure  
**Scale/Scope**: 
- 6 user stories (P1: 3, P2: 2, P3: 1)
- 46 functional requirements across 6 modules
- 4 major frontend components (Timecard Form, Calendar View, Project Entry Dialog, Summary)
- 5 backend service modules (Timecard, Project, Holiday, Report, System Config)
- Integration with 3 existing systems (Flowable, Process-Form Management, Organization)

## Constitution Check

*GATE: Must pass before Phase 0 research. Re-check after Phase 1 design.*

[Constitution check will be performed based on constitution.md rules - placeholder for actual constitution validation results]

## Project Structure

### Documentation (this feature)

```
specs/002-functional-specification-md/
├── plan.md              # This file (/speckit.plan command output)
├── spec.md              # Feature specification
├── checklists/
│   └── requirements.md  # Quality checklist
└── tasks.md             # Phase 2 output (/speckit.tasks command - NOT created by /speckit.plan)
```

### Source Code (repository root)

**Structure Decision**: Web application with separate frontend and backend directories, following existing SPMS project structure.

```
backend/
├── src/main/java/com/spms/backend/
│   ├── controller/
│   │   ├── timecard/          # Timecard REST API endpoints (to be created)
│   │   │   ├── TimecardController.java
│   │   │   ├── HolidayController.java
│   │   │   ├── ExcelController.java
│   │   │   └── ProjectTaskActivityController.java
│   │   ├── project/           # Project management (existing, may extend)
│   │   └── sys/               # System config (existing)
│   ├── controller/dto/
│   │   └── timecard/          # Timecard DTOs (to be created)
│   │       ├── ProjectTaskDTO.java
│   │       ├── ProjectValidationDTO.java
│   │       ├── HolidayDTO.java
│   │       ├── ImportResultDTO.java
│   │       └── ... (other DTOs)
│   ├── service/
│   │   ├── timecard/          # Timecard business logic (to be created)
│   │   │   ├── TimecardService.java
│   │   │   ├── HolidayService.java
│   │   │   ├── ProjectTaskActivityService.java
│   │   │   ├── ExcelImportExportService.java
│   │   │   └── impl/         # Service implementations
│   │   ├── project/           # Project management (existing, may extend)
│   │   └── report/            # Report generation service (to be created)
│   ├── repository/
│   │   ├── entities/
│   │   │   ├── timecard/      # Timecard entities (to be created)
│   │   │   │   └── HolidayEntity.java
│   │   │   └── process/       # ProcessDataEntity (existing, reuse)
│   │   ├── timecard/          # Timecard repositories (to be created)
│   │   │   └── HolidayRepository.java
│   │   └── process/           # ProcessDataRepository (existing, reuse)
│   └── model/
│       └── ProjectStatus.java # Project status enum (existing)
├── src/main/resources/
│   └── db/migration/          # Flyway migrations (if needed for system_config extensions)
└── src/test/java/
    └── timecard/               # Timecard service and controller tests (to be created)

frontend/
├── src/
│   ├── components/
│   │   ├── timecard/          # TimecardForm custom component
│   │   │   ├── TimecardForm.jsx
│   │   │   ├── CalendarView.jsx
│   │   │   ├── ProjectEntryDialog.jsx
│   │   │   └── TimecardSummary.jsx
│   │   ├── form/
│   │   │   └── custom-registry.jsx  # Register TimecardForm component
│   │   ├── holiday/           # HolidayManagement component
│   │   └── project/           # ProjectManagement component (may extend existing)
│   ├── pages/
│   │   ├── TimecardPage.jsx   # Main timecard entry page
│   │   ├── HolidayPage.jsx    # Holiday management page
│   │   └── ReportPage.jsx     # Report generation page
│   ├── api/
│   │   ├── timecard/          # Timecard API client
│   │   ├── holiday/           # Holiday API client
│   │   └── report/            # Report API client
│   └── utils/
│       └── timecardUtils.js # Timecard calculation and validation utilities
└── tests/
    └── components/
        └── timecard/           # Timecard component tests
```

## Complexity Tracking

*Fill ONLY if Constitution Check has violations that must be justified*

| Violation | Why Needed | Simpler Alternative Rejected Because |
|-----------|------------|-------------------------------------|
| Custom React component registration | Timecard requires complex calendar view and interactive entry that cannot be achieved with standard form-js schema | Standard form-js lacks calendar UI, project code autocomplete, and real-time summary calculation capabilities |
| Integration with Flowable BPMN | Approval workflow must leverage existing BPMN process engine for routing and task management | Building custom approval system would duplicate existing Flowable functionality and break system consistency |
| JSON storage in form_data | Timecard entries are variable-length arrays with complex nested structure (entries per date, summary calculations) | Normalized tables would require complex joins and lose atomicity of timecard submission |

## Implementation Phases

### Phase 0: Research & Setup (Prerequisites)

**Objectives**:
- Understand existing Process-Form Management custom component registration mechanism
- Review Flowable BPMN process instance creation and data storage patterns
- Analyze existing `spms_process_data` table usage and form_data JSON structure
- Review organizational hierarchy data model for manager lookup

**Deliverables**:
- Research notes on custom component registration workflow
- Data flow diagrams for timecard submission and approval
- Integration points documentation

**Dependencies**: None

### Phase 1: Foundation - Form Component & Registration (User Story 0)

**Objectives**:
- Create TimecardForm custom React component with basic structure
- Register component in custom-registry.jsx
- Create form version in Process-Form Management
- Verify form appears in BPMN process form selection

**Components to Create**:
- `frontend/src/components/timecard/TimecardForm.jsx` - Main form component (new)
- `frontend/src/components/form/custom-registry.jsx` - Component registration (extend existing)
- Form version creation via Process-Form Management UI

**Dependencies**: Process-Form Management system operational

**Acceptance Criteria**:
- Timecard form component visible in Process-Form Management
- Form version can be created and associated with BPMN process
- Form component receives initialData prop correctly

### Phase 2: Core Timecard Entry (User Story 1 - Part 1)

**Objectives**:
- Implement employee basic information display and auto-population
- Create Calendar View component with monthly view
- Implement data rendering logic (Flowable vs API)
- Add date cell click handler and ProjectEntryDialog

**Components to Create**:
- `frontend/src/components/timecard/CalendarView.jsx` - Calendar grid display (new)
- `frontend/src/components/timecard/ProjectEntryDialog.jsx` - Entry dialog (new)
- `backend/src/main/java/com/spms/backend/controller/timecard/TimecardController.java` - Add employee-info endpoint
- Backend API: `GET /api/v1/timecard/employee-info` - Fetch employee info (new)
- Backend API: `GET /api/v1/process/process-instances/{id}/form-data` - Get Flowable data (existing)

**Dependencies**: Phase 1 complete, Organization module data available

**Acceptance Criteria**:
- Employee info auto-populates from API when no Flowable data exists
- Calendar displays monthly view correctly
- Clicking date cell opens ProjectEntryDialog
- Flowable data renders correctly when process instance exists

### Phase 3: Timecard Entry - Validation & Summary (User Story 1 - Part 2)

**Objectives**:
- Implement project code validation and auto-population (task, activity)
- Add project code search/autocomplete functionality
- Implement real-time summary calculation (grouped by Project+Task+Activity)
- Add visual warnings (red for daily >8h/weekend, yellow for monthly >160h)
- Implement holiday highlighting (green background)

**Components to Create**:
- `frontend/src/components/timecard/TimecardSummary.jsx` - Summary display (new)
- `frontend/src/utils/timecardUtils.js` - Calculation and validation utilities (new)
- `backend/src/main/java/com/spms/backend/controller/timecard/TimecardController.java` - Add project validation endpoints
- `backend/src/main/java/com/spms/backend/service/timecard/TimecardService.java` - Implement project validation logic
- Backend API: `GET /api/v1/timecard/projects/validate/{projectCode}` - Project validation (new)
- Backend API: `GET /api/v1/projects` - Project search (existing, may extend)
- Backend API: `GET /api/v1/sys/config?category=HOLIDAY` - Holiday data (existing)

**Dependencies**: Phase 2 complete, Project module operational

**Acceptance Criteria**:
- Project code validation works correctly
- Summary calculates totals in real-time
- Visual warnings display appropriately
- Holidays highlighted in green

### Phase 4: Timecard Submission & Storage (User Story 1 - Part 3)

**Objectives**:
- Implement timecard data structure (employee_info, timecard_entries, summary)
- Create backend service to save timecard to `spms_process_data`
- Implement Flowable process instance creation on submission
- Add monthly deadline validation
- Support resubmission (latest version precedence)

**Components to Create**:
- `backend/src/main/java/com/spms/backend/service/timecard/TimecardService.java` - Implement timecard submission logic (new)
- `backend/src/main/java/com/spms/backend/service/timecard/impl/TimecardServiceImpl.java` - Service implementation (new)
- `backend/src/main/java/com/spms/backend/controller/timecard/TimecardController.java` - Add submit endpoint (extend)
- `backend/src/main/java/com/spms/backend/controller/dto/timecard/` - Add timecard submission DTOs (new)
- Backend API: `POST /api/v1/timecard/submit` - Submit timecard (new)
- Backend API: `POST /api/v1/process/process-instances` - Create process instance (existing)
- Backend API: `GET /api/v1/sys/config?key=timecard.deadline.{month}` - Deadline lookup (existing)

**Dependencies**: Phase 3 complete, Flowable engine available

**Acceptance Criteria**:
- Timecard data saved to `spms_process_data` with correct JSON structure
- Process instance created in Flowable
- Deadline validation prevents late submissions
- Resubmission updates existing process instance

### Phase 5: Batch Operations & Month Adjustment (User Story 1 - Part 4)

**Objectives**:
- Implement batch timecard entry (copy template date to target range)
- Add "preview month adjust" functionality for previous month edits
- Implement batch operation UI controls

**Components to Create**:
- Batch fill functionality in `TimecardForm.jsx` (extend existing)
- Month adjustment dialog component (new)
- `backend/src/main/java/com/spms/backend/controller/timecard/TimecardController.java` - Add adjust endpoint
- `backend/src/main/java/com/spms/backend/service/timecard/TimecardService.java` - Add month adjustment logic
- Backend API: `PUT /api/v1/timecard/adjust/{month}` - Month adjustment (new)

**Dependencies**: Phase 4 complete

**Acceptance Criteria**:
- Batch fill copies entries correctly to target dates
- Month adjustment only allows previous month edits
- Existing entries not overwritten unless explicitly allowed

### Phase 6: Approval Workflow Integration (User Story 2)

**Objectives**:
- Ensure approvers can view timecard with all visual warnings
- Verify BPMN process routing works with organizational hierarchy
- Test approval/rejection with feedback
- Verify email/notification system integration

**Components to Create**:
- TimecardForm read-only mode for approvers (extend existing component)
- Integration with existing Flowable task completion endpoints (existing)
- `backend/src/main/java/com/spms/backend/controller/timecard/TimecardController.java` - Add approval-tasks endpoint
- `backend/src/main/java/com/spms/backend/service/timecard/TimecardService.java` - Add approval task query logic
- Backend API: `GET /api/v1/timecard/approval-tasks` - List pending approvals (new)

**Dependencies**: Phase 4 complete, BPMN process configured, Email system available

**Acceptance Criteria**:
- Approvers see same visual warnings as employees
- Approval routing works based on organizational hierarchy
- Rejection with feedback returns to employee
- Notifications sent correctly

### Phase 7: Project Management (User Story 4)

**Objectives**:
- Extend existing project management with status field (active/completed)
- Implement project search by code
- Add Excel import/export for projects
- Filter active projects in timecard project selection

**Components to Create/Extend**:
- Extend `backend/src/main/java/com/spms/backend/service/project/ProjectService.java` - Add active projects filter
- Extend `backend/src/main/java/com/spms/backend/controller/project/ProjectController.java` - Add active projects endpoint
- Frontend project management page updates (extend existing)
- Backend API: `GET /api/v1/timecard/projects/active` - Active projects only (new, in TimecardController)
- Excel import/export functionality (extend existing project module)

**Dependencies**: Existing project module, Phase 3 complete

**Acceptance Criteria**:
- Project status field works correctly
- Only active projects appear in timecard selection
- Excel import/export functions properly

### Phase 8: Holiday Management (User Story 5)

**Objectives**:
- Create holiday management calendar interface
- Implement holiday CRUD operations
- Store holidays in `system_config` table
- Integrate holiday data with timecard calendar view
- Add Excel import/export for holidays

**Components to Create**:
- `frontend/src/components/holiday/HolidayManagement.jsx` (new)
- `backend/src/main/java/com/spms/backend/service/timecard/HolidayService.java` (new)
- `backend/src/main/java/com/spms/backend/service/timecard/impl/HolidayServiceImpl.java` (new)
- `backend/src/main/java/com/spms/backend/controller/timecard/HolidayController.java` (new)
- `backend/src/main/java/com/spms/backend/repository/timecard/HolidayRepository.java` (new)
- `backend/src/main/java/com/spms/backend/repository/entities/timecard/HolidayEntity.java` (new)
- `backend/src/main/java/com/spms/backend/controller/dto/timecard/HolidayDTO.java` (new)
- `backend/src/main/java/com/spms/backend/service/timecard/ExcelImportExportService.java` - Add holiday import/export (new)
- Backend API: `GET /api/v1/timecard/holidays?month={month}` - Get holidays (new)
- Backend API: `POST /api/v1/timecard/holidays` - Create holiday (new)
- Backend API: `POST /api/v1/timecard/holidays/import` - Import Excel (new)

**Dependencies**: Phase 3 complete (for calendar integration)

**Acceptance Criteria**:
- Holidays can be added/edited/deleted
- Holidays display in timecard calendar with green background
- Multi-country holiday support works
- Excel import/export functions properly

### Phase 9: Report Generation (User Story 6)

**Objectives**:
- Implement Timecard Summary report
- Implement Individual Timecard Record report
- Implement Timecard Details Report
- Implement Perm Staff IT Record report
- Add filtering and Excel export functionality

**Components to Create**:
- `frontend/src/pages/ReportPage.jsx` (new)
- `backend/src/main/java/com/spms/backend/service/report/ReportService.java` (new)
- `backend/src/main/java/com/spms/backend/service/report/impl/ReportServiceImpl.java` (new)
- `backend/src/main/java/com/spms/backend/controller/report/ReportController.java` (new)
- `backend/src/main/java/com/spms/backend/controller/dto/report/` - Report DTOs (new)
- Backend API: `GET /api/v1/report/timecard-summary` - Summary report (new)
- Backend API: `GET /api/v1/report/individual-timecard` - Individual report (new)
- Backend API: `GET /api/v1/report/timecard-details` - Details report (new)
- Backend API: `GET /api/v1/report/perm-staff-it` - OT report (new)

**Dependencies**: Phase 4 complete (approved timecards available)

**Acceptance Criteria**:
- All report types generate correctly
- Filtering works for all report fields
- Excel export matches defined report headers
- Permission control works (employees see only own, admins see all)

### Phase 10: Organization Data Management (User Story 3)

**Objectives**:
- Verify organization module supports local/functional manager fields
- Implement Excel import/export for organization data
- Add audit logging for organization operations
- Verify approval routing uses organization data correctly

**Components to Create/Extend**:
- Extend existing organization management (if needed)
- Backend API: `POST /api/v1/organization/import` - Import Excel (new, if not exists)
- Backend API: `GET /api/v1/organization/export` - Export Excel (new, if not exists)

**Dependencies**: Organization module (existing)

**Acceptance Criteria**:
- Local and functional manager fields accessible
- Excel import/export works
- Audit logs record all operations
- Approval routing uses organization data correctly

## Data Model

### Existing Tables (Reuse)

- **spms_process_data**: Stores timecard data with `form_type='TIMECARD'`
  - `form_data` (TEXT/JSONB): Complete timecard JSON structure
  - `process_instance_id`: Links to Flowable process instance
  - `user_id`: Employee who submitted
  - `status`: DRAFT, SUBMITTED, APPROVED, REJECTED

- **spms_user**: Employee information
  - `id`: Staff ID
  - `user_profiles` (JSONB): Contains staff_name_chinese, staff_name_english

- **spms_department**: Organizational hierarchy
  - `department_head_id`: Department head
  - `local_manager_id` (via tags): Local manager
  - `functional_manager_id` (via tags): Functional manager

- **spms_project**: Project definitions
  - `project_code`: Unique project identifier
  - `status`: ACTIVE or COMPLETED
  - `is_active`: Active flag

- **spms_task**: Task definitions
  - `task_number`: Task identifier
  - `project_id`: Links to project
  - `activity`: Activity description

- **system_config**: System configuration
  - `key`: Configuration key (e.g., "timecard.deadline.2025-01", "holiday.2025-01")
  - `value`: JSON configuration data
  - `category`: HOLIDAY, TIMECARD, etc.

### Data Flow

1. **Timecard Submission**:
   - Frontend → `POST /api/v1/timecard/submit` → TimecardService
   - TimecardService → ProcessService → Flowable Engine (create instance)
   - TimecardService → ProcessDataRepository → Save to `spms_process_data`

2. **Timecard Retrieval**:
   - Frontend → `GET /api/v1/process/process-instances/{id}/form-data` → ProcessService
   - ProcessService → Flowable Engine → Get process instance data
   - If no Flowable data: Frontend → `GET /api/v1/timecard/employee-info` → UserService

3. **Approval Routing**:
   - Flowable Engine → ProcessService → DepartmentService
   - DepartmentService → Lookup `local_manager_id`, `functional_manager_id`
   - Flowable Engine → Assign tasks to managers

4. **Report Generation**:
   - Frontend → `GET /api/v1/report/{type}` → ReportService
   - ReportService → ProcessDataRepository → Query `spms_process_data` with filters
   - ReportService → Join with `spms_user`, `spms_department`, `spms_project`
   - ReportService → Generate Excel → Return to frontend

## Integration Points

### 1. Process-Form Management Integration
- **Custom Component Registration**: Register TimecardForm in `custom-registry.jsx`
- **Form Version Creation**: Create form version via Process-Form Management UI
- **Form Association**: Link form version to BPMN process definition

### 2. Flowable BPMN Engine Integration
- **Process Instance Creation**: Use existing `POST /api/v1/process/process-instances`
- **Form Data Storage**: Store timecard JSON in `form_data` field
- **Task Assignment**: Flowable handles task routing based on BPMN definition
- **Approval Status**: Flowable updates process instance status

### 3. Organizational Hierarchy Integration
- **Manager Lookup**: Query `spms_department` for `local_manager_id`, `functional_manager_id`
- **Employee Info**: Query `spms_user` and `spms_department` for employee details
- **Approval Routing**: BPMN process uses organizational data for task assignment

### 4. Project Management Integration
- **Project Validation**: Query `spms_project` for active projects
- **Task Lookup**: Query `spms_task` by project code
- **Activity Retrieval**: Get activity from task record

## Testing Strategy

### Unit Tests (to be created)
- TimecardService: Data transformation, validation logic
- TimecardUtils: Summary calculation, date range validation
- ReportService: Report data aggregation, filtering
- HolidayService: Holiday CRUD operations

### Integration Tests (to be created)
- TimecardController: API endpoints, request/response handling
- Flowable Integration: Process instance creation, task assignment
- Database: ProcessDataRepository queries, data persistence
- HolidayController: Holiday management endpoints

### Component Tests (Frontend, to be created)
- TimecardForm: Component rendering, data flow
- CalendarView: Date selection, visual warnings
- ProjectEntryDialog: Form validation, auto-population
- HolidayManagement: Holiday CRUD operations

### End-to-End Tests (to be created)
- Complete timecard submission workflow
- Approval workflow with manager routing
- Report generation with filters
- Holiday management workflow

## Risk Mitigation

### Technical Risks
1. **Flowable Integration Complexity**: Mitigate by using existing ProcessService patterns
2. **JSON Data Structure Changes**: Mitigate by versioning form_data structure
3. **Performance with Large Datasets**: Mitigate by pagination and indexing

### Business Risks
1. **Data Migration from Old System**: Mitigate by phased rollout
2. **User Adoption**: Mitigate by comprehensive training and documentation
3. **Approval Routing Errors**: Mitigate by thorough testing with organizational hierarchy

## Success Metrics

- **SC-001**: Employees complete timecard entry in <15 minutes
- **SC-002**: Approval routing within 30 seconds
- **SC-003**: 95% first-attempt validation success
- **SC-004**: Managers approve/reject in <2 minutes
- **SC-005**: Support 500 concurrent users
- **SC-006**: Report generation <10 seconds for 10K entries
- **SC-007**: Excel operations <30 seconds for 5K records
- **SC-008**: 90% project code search success
- **SC-009**: 99.9% approval routing accuracy
- **SC-010**: 100% visual warning accuracy
