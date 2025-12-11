# Tasks: Timecard Module Refactoring

**Input**: Design documents from `/specs/002-functional-specification-md/`
**Prerequisites**: plan.md (required), spec.md (required for user stories)

**Tests**: Tests are included as they are critical for quality assurance per constitution requirements.

**Organization**: Tasks are grouped by user story to enable independent implementation and testing of each story.

## Format: `[ID] [P?] [Story] Description`
- **[P]**: Can run in parallel (different files, no dependencies)
- **[Story]**: Which user story this task belongs to (e.g., US0, US1, US2)
- Include exact file paths in descriptions

## Path Conventions
- **Web app**: `backend/src/`, `frontend/src/`
- Paths shown below follow web application structure

---

## Phase 1: Setup (Shared Infrastructure)

**Purpose**: Project initialization and basic structure

- [ ] T001 Create backend timecard package structure: `backend/src/main/java/com/spms/backend/controller/timecard/`, `backend/src/main/java/com/spms/backend/service/timecard/`, `backend/src/main/java/com/spms/backend/repository/timecard/`
- [ ] T002 [P] Create frontend timecard component structure: `frontend/src/components/timecard/`, `frontend/src/pages/`, `frontend/src/api/timecard/`
- [ ] T003 [P] Create DTO package structure: `backend/src/main/java/com/spms/backend/controller/dto/timecard/`
- [ ] T004 [P] Setup test structure: `backend/src/test/java/timecard/`, `frontend/tests/components/timecard/`

---

## Phase 2: Foundational (Blocking Prerequisites)

**Purpose**: Core infrastructure that MUST be complete before ANY user story can be implemented

**⚠️ CRITICAL**: No user story work can begin until this phase is complete

- [ ] T005 Review existing Process-Form Management custom component registration mechanism in `frontend/src/components/form/custom-registry.jsx`
- [ ] T006 Review existing Flowable BPMN process instance creation patterns in `backend/src/main/java/com/spms/backend/service/process/ProcessService.java`
- [ ] T007 Review existing `spms_process_data` table structure and ProcessDataEntity usage
- [ ] T008 Review organizational hierarchy data model in `spms_department` table for manager lookup
- [ ] T009 [P] Create base DTO classes: `backend/src/main/java/com/spms/backend/controller/dto/timecard/TimecardSubmissionDTO.java`, `backend/src/main/java/com/spms/backend/controller/dto/timecard/EmployeeInfoDTO.java`
- [ ] T010 [P] Create timecard utility constants: `backend/src/main/java/com/spms/backend/service/timecard/TimecardConstants.java` (form_type='TIMECARD', status enums, claim types)

**Checkpoint**: Foundation ready - user story implementation can now begin in parallel

---

## Phase 3: User Story 0 - Timecard Form Setup and Registration (Priority: P1) 🎯 MVP Prerequisite

**Goal**: Create and register Timecard form as custom component so it can be associated with BPMN processes

**Independent Test**: Create Timecard custom form component, register it in custom-registry.jsx, create form version in Process-Form Management, verify form appears in BPMN process form selection

### Tests for User Story 0

- [ ] T011 [P] [US0] Component registration test: Verify TimecardForm appears in custom-registry in `frontend/tests/components/form/custom-registry.test.jsx`
- [ ] T012 [P] [US0] Form component rendering test: Test TimecardForm receives initialData prop correctly in `frontend/tests/components/timecard/TimecardForm.test.jsx`

### Implementation for User Story 0

- [ ] T013 [US0] Create basic TimecardForm component structure in `frontend/src/components/timecard/TimecardForm.jsx` with props: initialData, readOnly, onSubmit
- [ ] T014 [US0] Register TimecardForm in `frontend/src/components/form/custom-registry.jsx` with componentKey 'TimecardForm'
- [ ] T015 [US0] Add TimecardForm to customFormCatalog array in `frontend/src/components/form/custom-registry.jsx` with metadata (name, description, category, icon)
- [ ] T016 [US0] Verify TimecardForm appears in Process-Form Management form selection list (manual verification via UI)
- [ ] T017 [US0] Create form version in Process-Form Management UI with form_type='TIMECARD' (manual step, document process)

**Checkpoint**: At this point, User Story 0 should be complete - Timecard form is registered and can be associated with BPMN processes

---

## Phase 4: User Story 1 - Employee Timecard Entry (Priority: P1) 🎯 MVP Core

**Goal**: Enable employees to fill out timecard with daily work hours, view calendar, add project entries, and submit for approval

**Independent Test**: Employee logs in, views timecard calendar, adds project entries with hours, saves/submits timecard. Delivers primary value of time tracking.

### Tests for User Story 1

- [ ] T018 [P] [US1] Unit test for employee info API endpoint in `backend/src/test/java/timecard/TimecardControllerTest.java`
- [ ] T019 [P] [US1] Unit test for timecard submission service in `backend/src/test/java/timecard/TimecardServiceTest.java`
- [ ] T020 [P] [US1] Integration test for timecard submission workflow in `backend/src/test/java/timecard/TimecardIntegrationTest.java`
- [ ] T021 [P] [US1] Component test for CalendarView in `frontend/tests/components/timecard/CalendarView.test.jsx`
- [ ] T022 [P] [US1] Component test for ProjectEntryDialog in `frontend/tests/components/timecard/ProjectEntryDialog.test.jsx`
- [ ] T023 [P] [US1] Component test for TimecardSummary in `frontend/tests/components/timecard/TimecardSummary.test.jsx`
- [ ] T024 [P] [US1] Unit test for timecardUtils calculation functions in `frontend/tests/utils/timecardUtils.test.js`

### Implementation for User Story 1 - Part 1: Basic Display & Data Loading

- [ ] T025 [US1] Create EmployeeInfoDTO in `backend/src/main/java/com/spms/backend/controller/dto/timecard/EmployeeInfoDTO.java` with fields: staffId, staffNameChinese, staffNameEnglish, team, timecardMonth, staffType, departmentHead, finalApprover
- [ ] T026 [US1] Create TimecardController in `backend/src/main/java/com/spms/backend/controller/timecard/TimecardController.java` with base structure
- [ ] T027 [US1] Implement GET /api/v1/timecard/employee-info endpoint in TimecardController that queries spms_user and spms_department tables
- [ ] T028 [US1] Create TimecardService interface in `backend/src/main/java/com/spms/backend/service/timecard/TimecardService.java`
- [ ] T029 [US1] Implement TimecardServiceImpl in `backend/src/main/java/com/spms/backend/service/timecard/impl/TimecardServiceImpl.java` with getEmployeeInfo method
- [ ] T030 [US1] Create API client function in `frontend/src/api/timecard/timecardApi.js` for GET /api/v1/timecard/employee-info
- [ ] T031 [US1] Implement data rendering logic in TimecardForm.jsx: check Flowable process instance for data, if exists render from Flowable, else fetch from employee-info API
- [ ] T032 [US1] Create CalendarView component in `frontend/src/components/timecard/CalendarView.jsx` with monthly grid layout
- [ ] T033 [US1] Implement date cell click handler in CalendarView that opens ProjectEntryDialog
- [ ] T034 [US1] Create ProjectEntryDialog component in `frontend/src/components/timecard/ProjectEntryDialog.jsx` with fields: project code, task, activity, claim type, hours, remark

### Implementation for User Story 1 - Part 2: Validation & Summary

- [ ] T035 [US1] Create ProjectTaskDTO in `backend/src/main/java/com/spms/backend/controller/dto/timecard/ProjectTaskDTO.java` with projectCode, taskNumber, activity fields
- [ ] T036 [US1] Create ProjectValidationDTO in `backend/src/main/java/com/spms/backend/controller/dto/timecard/ProjectValidationDTO.java` for validation response
- [ ] T037 [US1] Implement GET /api/v1/timecard/projects/validate/{projectCode} endpoint in TimecardController that validates project exists and is active
- [ ] T038 [US1] Add project validation logic in TimecardService that queries spms_project and spms_task tables
- [ ] T039 [US1] Create API client function in `frontend/src/api/timecard/timecardApi.js` for project validation
- [ ] T040 [US1] Implement project code autocomplete/search in ProjectEntryDialog with validation
- [ ] T041 [US1] Implement auto-population of task and activity fields when project code is validated
- [ ] T042 [US1] Create timecardUtils.js in `frontend/src/utils/timecardUtils.js` with functions: calculateSummary, validateHours, groupByProjectTaskActivity
- [ ] T043 [US1] Create TimecardSummary component in `frontend/src/components/timecard/TimecardSummary.jsx` that displays grouped totals
- [ ] T044 [US1] Implement real-time summary calculation in TimecardForm that updates when entries change
- [ ] T045 [US1] Implement visual warning logic: red background for dates with >8 hours or weekend entries
- [ ] T046 [US1] Implement visual warning logic: yellow background for summary when total >160 hours
- [ ] T047 [US1] Create API client function in `frontend/src/api/timecard/timecardApi.js` for GET /api/v1/sys/config?category=HOLIDAY
- [ ] T048 [US1] Implement holiday highlighting: green background for holiday dates in CalendarView

### Implementation for User Story 1 - Part 3: Submission & Storage

- [ ] T049 [US1] Create TimecardSubmissionDTO in `backend/src/main/java/com/spms/backend/controller/dto/timecard/TimecardSubmissionDTO.java` matching form_data JSON structure (employee_info, timecard_entries, summary)
- [ ] T050 [US1] Implement POST /api/v1/timecard/submit endpoint in TimecardController
- [ ] T051 [US1] Add submitTimecard method in TimecardService that validates deadline, creates/updates ProcessDataEntity, creates Flowable process instance
- [ ] T052 [US1] Implement deadline validation in TimecardService: query system_config for timecard.deadline.{month} key
- [ ] T053 [US1] Implement ProcessDataEntity save logic in TimecardService: set form_type='TIMECARD', store JSON in form_data field
- [ ] T054 [US1] Integrate with ProcessService to create Flowable process instance on submission
- [ ] T055 [US1] Implement resubmission logic: update existing process instance if one exists for same user and month
- [ ] T056 [US1] Create API client function in `frontend/src/api/timecard/timecardApi.js` for POST /api/v1/timecard/submit
- [ ] T057 [US1] Implement submit handler in TimecardForm that constructs JSON payload and calls submit API
- [ ] T058 [US1] Add status tracking display in TimecardForm showing: draft, submitted, approval pending, approved

### Implementation for User Story 1 - Part 4: Batch Operations & Month Adjustment

- [ ] T059 [US1] Implement batch fill functionality in TimecardForm: select template date, choose scope (current week/all working days/blank days), copy entries to target dates
- [ ] T060 [US1] Add batch fill UI controls in TimecardForm with scope selection dropdown
- [ ] T061 [US1] Create MonthAdjustmentDialog component in `frontend/src/components/timecard/MonthAdjustmentDialog.jsx` for previous month edits
- [ ] T062 [US1] Implement PUT /api/v1/timecard/adjust/{month} endpoint in TimecardController (only allows previous month)
- [ ] T063 [US1] Add month adjustment logic in TimecardService that validates month is previous month
- [ ] T064 [US1] Create API client function in `frontend/src/api/timecard/timecardApi.js` for PUT /api/v1/timecard/adjust/{month}
- [ ] T065 [US1] Add "preview month adjust" button in TimecardForm that opens MonthAdjustmentDialog

**Checkpoint**: At this point, User Story 1 should be fully functional - employees can enter, validate, submit timecards with all features

---

## Phase 5: User Story 2 - Timecard Approval Workflow (Priority: P1) 🎯 MVP Core

**Goal**: Enable managers to review and approve employee timecards with visual warnings, routing based on organizational hierarchy

**Independent Test**: Manager logs in, views pending timecard approvals, sees visual warnings, approves/rejects with comments, system routes to next approver automatically

### Tests for User Story 2

- [ ] T066 [P] [US2] Integration test for approval task listing in `backend/src/test/java/timecard/ApprovalIntegrationTest.java`
- [ ] T067 [P] [US2] Component test for TimecardForm read-only mode in `frontend/tests/components/timecard/TimecardForm.readonly.test.jsx`
- [ ] T068 [P] [US2] E2E test for approval workflow: submit → route → approve in `backend/src/test/java/timecard/ApprovalWorkflowE2ETest.java`

### Implementation for User Story 2

- [ ] T069 [US2] Implement GET /api/v1/timecard/approval-tasks endpoint in TimecardController that queries Flowable for pending tasks assigned to current user
- [ ] T070 [US2] Add getApprovalTasks method in TimecardService that integrates with Flowable TaskService
- [ ] T071 [US2] Implement read-only mode in TimecardForm: add readOnly prop, disable all input fields, show approval/reject buttons
- [ ] T072 [US2] Ensure visual warnings (red/yellow backgrounds) display correctly in read-only mode
- [ ] T073 [US2] Integrate with existing Flowable task completion endpoints: POST /api/v1/process/process-instances/{instanceId}/tasks/{taskId}/complete
- [ ] T074 [US2] Integrate with existing Flowable task rejection endpoint: POST /api/v1/process/process-instances/{instanceId}/tasks/{taskId}/reject
- [ ] T075 [US2] Add approval/rejection UI in TimecardForm read-only mode with comment field
- [ ] T076 [US2] Verify BPMN process routing works: system looks up local_manager_id and functional_manager_id from spms_department table (this is handled by existing BPMN configuration, verify integration)

**Checkpoint**: At this point, User Story 2 should be complete - managers can approve/reject timecards with proper routing

---

## Phase 6: User Story 4 - Project Management (Priority: P2)

**Goal**: Extend project management with status field, search, and Excel import/export, filter active projects in timecard selection

**Independent Test**: Admin creates/edits projects with status, searches by code, imports/exports Excel, verifies only active projects appear in timecard selection

### Tests for User Story 4

- [ ] T077 [P] [US4] Unit test for active projects filter in `backend/src/test/java/project/ProjectServiceTest.java`
- [ ] T078 [P] [US4] Integration test for project Excel import/export in `backend/src/test/java/project/ProjectImportExportTest.java`

### Implementation for User Story 4

- [ ] T079 [US4] Verify ProjectEntity has status field (ACTIVE/COMPLETED) - extend if needed in `backend/src/main/java/com/spms/backend/repository/entities/project/ProjectEntity.java`
- [ ] T080 [US4] Extend ProjectService in `backend/src/main/java/com/spms/backend/service/project/ProjectService.java` to add filterActiveProjects method
- [ ] T081 [US4] Implement GET /api/v1/timecard/projects/active endpoint in TimecardController that returns only active projects
- [ ] T082 [US4] Extend ProjectController in `backend/src/main/java/com/spms/backend/controller/project/ProjectController.java` to add search by project code if not exists
- [ ] T083 [US4] Create ExcelImportExportService in `backend/src/main/java/com/spms/backend/service/timecard/ExcelImportExportService.java` with project import/export methods
- [ ] T084 [US4] Implement POST /api/v1/project/import endpoint in ProjectController for Excel import
- [ ] T085 [US4] Implement GET /api/v1/project/export endpoint in ProjectController for Excel export
- [ ] T086 [US4] Update frontend project management page to include status field in project form
- [ ] T087 [US4] Update frontend project management page to add Excel import/export buttons
- [ ] T088 [US4] Update timecard project selection to filter only active projects (modify ProjectEntryDialog to use /api/v1/timecard/projects/active endpoint)

**Checkpoint**: At this point, User Story 4 should be complete - project management extended with required features

---

## Phase 7: User Story 5 - Holiday Management (Priority: P2)

**Goal**: Create holiday management calendar interface, CRUD operations, store in system_config, integrate with timecard calendar, Excel import/export

**Independent Test**: Admin views holiday calendar, adds/edits/deletes holidays, imports/exports Excel, verifies holidays appear in timecard calendar with green background

### Tests for User Story 5

- [ ] T089 [P] [US5] Unit test for HolidayService CRUD operations in `backend/src/test/java/timecard/HolidayServiceTest.java`
- [ ] T090 [P] [US5] Integration test for holiday API endpoints in `backend/src/test/java/timecard/HolidayControllerTest.java`
- [ ] T091 [P] [US5] Component test for HolidayManagement in `frontend/tests/components/holiday/HolidayManagement.test.jsx`

### Implementation for User Story 5

- [ ] T092 [US5] Create HolidayDTO in `backend/src/main/java/com/spms/backend/controller/dto/timecard/HolidayDTO.java` with fields: date, country, name, type
- [ ] T093 [US5] Create HolidayService interface in `backend/src/main/java/com/spms/backend/service/timecard/HolidayService.java`
- [ ] T094 [US5] Implement HolidayServiceImpl in `backend/src/main/java/com/spms/backend/service/timecard/impl/HolidayServiceImpl.java` that stores holidays in system_config table with key format "holiday.{date}.{country}"
- [ ] T095 [US5] Create HolidayController in `backend/src/main/java/com/spms/backend/controller/timecard/HolidayController.java`
- [ ] T096 [US5] Implement GET /api/v1/timecard/holidays?month={month} endpoint in HolidayController
- [ ] T097 [US5] Implement POST /api/v1/timecard/holidays endpoint in HolidayController for creating holidays
- [ ] T098 [US5] Implement PUT /api/v1/timecard/holidays/{id} endpoint in HolidayController for updating holidays
- [ ] T099 [US5] Implement DELETE /api/v1/timecard/holidays/{id} endpoint in HolidayController
- [ ] T100 [US5] Add holiday import/export methods to ExcelImportExportService
- [ ] T101 [US5] Implement POST /api/v1/timecard/holidays/import endpoint in HolidayController for Excel import
- [ ] T102 [US5] Implement GET /api/v1/timecard/holidays/export?month={month} endpoint in HolidayController for Excel export
- [ ] T103 [US5] Create HolidayManagement component in `frontend/src/components/holiday/HolidayManagement.jsx` with calendar view similar to TimecardForm
- [ ] T104 [US5] Implement holiday CRUD operations UI in HolidayManagement component
- [ ] T105 [US5] Create API client functions in `frontend/src/api/timecard/holidayApi.js` for all holiday endpoints
- [ ] T106 [US5] Integrate holiday data with TimecardForm CalendarView: fetch holidays and display green background
- [ ] T107 [US5] Create HolidayPage in `frontend/src/pages/HolidayPage.jsx` that uses HolidayManagement component

**Checkpoint**: At this point, User Story 5 should be complete - holiday management fully functional and integrated with timecard

---

## Phase 8: User Story 6 - Timecard Reports Generation (Priority: P3)

**Goal**: Generate various timecard reports (Summary, Individual Record, Details, Perm Staff IT) with filtering and Excel export

**Independent Test**: Manager/admin selects report type, applies filters, generates report, exports to Excel. Reports show accurate data with proper permission control.

### Tests for User Story 6

- [ ] T108 [P] [US6] Unit test for ReportService data aggregation in `backend/src/test/java/report/ReportServiceTest.java`
- [ ] T109 [P] [US6] Integration test for report generation endpoints in `backend/src/test/java/report/ReportControllerTest.java`
- [ ] T110 [P] [US6] Component test for ReportPage in `frontend/tests/pages/ReportPage.test.jsx`

### Implementation for User Story 6

- [ ] T111 [US6] Create report DTOs in `backend/src/main/java/com/spms/backend/controller/dto/report/`: TimecardSummaryDTO, IndividualTimecardDTO, TimecardDetailsDTO, PermStaffITDTO
- [ ] T112 [US6] Create ReportService interface in `backend/src/main/java/com/spms/backend/service/report/ReportService.java`
- [ ] T113 [US6] Implement ReportServiceImpl in `backend/src/main/java/com/spms/backend/service/report/impl/ReportServiceImpl.java`
- [ ] T114 [US6] Implement Timecard Summary report logic: query spms_process_data with form_type='TIMECARD', group by project, department, time period
- [ ] T115 [US6] Implement Individual Timecard Record report logic: query by user_id (employees see own, admins see all)
- [ ] T116 [US6] Implement Timecard Details Report logic: join spms_process_data with spms_user, spms_department, spms_project for detailed view
- [ ] T117 [US6] Implement Perm Staff IT Record report logic: filter by staff_type (PERMANENT or VENDOR), extract only OT data (claim_type contains OT)
- [ ] T118 [US6] Create ReportController in `backend/src/main/java/com/spms/backend/controller/report/ReportController.java`
- [ ] T119 [US6] Implement GET /api/v1/report/timecard-summary endpoint with filters: project, department, timePeriod
- [ ] T120 [US6] Implement GET /api/v1/report/individual-timecard endpoint with filters: userId (optional), timePeriod, permission check
- [ ] T121 [US6] Implement GET /api/v1/report/timecard-details endpoint with filters: project, department, timePeriod, staffType
- [ ] T122 [US6] Implement GET /api/v1/report/perm-staff-it endpoint with filters: timePeriod
- [ ] T123 [US6] Add Excel export functionality to all report endpoints (return Excel file)
- [ ] T124 [US6] Create ReportPage component in `frontend/src/pages/ReportPage.jsx` with report type tabs and filter controls
- [ ] T125 [US6] Implement report type selection UI (Summary, Individual Record, Details, Perm Staff IT)
- [ ] T126 [US6] Implement filter controls for each report type (project, department, time period, staff type)
- [ ] T127 [US6] Create API client functions in `frontend/src/api/report/reportApi.js` for all report endpoints
- [ ] T128 [US6] Implement report data display in ReportPage with table/grid view
- [ ] T129 [US6] Implement Excel export button in ReportPage that downloads report as Excel file
- [ ] T130 [US6] Implement permission control: employees see only own Individual Timecard Record, admins see all

**Checkpoint**: At this point, User Story 6 should be complete - all report types functional with filtering and export

---

## Phase 9: User Story 3 - Organization Data Management (Priority: P2)

**Goal**: Verify organization module supports local/functional manager fields, implement Excel import/export, add audit logging

**Independent Test**: Admin imports organization data from Excel, views/edits department info including manager assignments, exports data, verifies changes reflected in timecard routing

### Tests for User Story 3

- [ ] T131 [P] [US3] Integration test for organization Excel import/export in `backend/src/test/java/idm/OrganizationImportExportTest.java`

### Implementation for User Story 3

- [ ] T132 [US3] Verify spms_department table supports local_manager_id and functional_manager_id (check if via tags or direct fields)
- [ ] T133 [US3] Verify DepartmentService can read/write manager fields - extend if needed in `backend/src/main/java/com/spms/backend/service/idm/DepartmentService.java`
- [ ] T134 [US3] Implement POST /api/v1/organization/import endpoint in DepartmentController (or create OrganizationController) for Excel import if not exists
- [ ] T135 [US3] Implement GET /api/v1/organization/export endpoint for Excel export if not exists
- [ ] T136 [US3] Add organization import/export methods to ExcelImportExportService
- [ ] T137 [US3] Verify audit logging for organization operations: check if UserActivity logging exists, extend if needed
- [ ] T138 [US3] Verify approval routing uses organization data: test that BPMN process looks up managers from spms_department correctly (integration verification)

**Checkpoint**: At this point, User Story 3 should be complete - organization data management supports timecard requirements

---

## Phase 10: Polish & Cross-Cutting Concerns

**Purpose**: Improvements that affect multiple user stories

- [ ] T139 [P] Add comprehensive error handling and user-friendly error messages across all timecard components
- [ ] T140 [P] Add loading states and progress indicators for async operations (API calls, Excel import/export)
- [ ] T141 [P] Implement input validation and sanitization for all user inputs (project codes, hours, dates)
- [ ] T142 [P] Add comprehensive logging for timecard operations in backend services
- [ ] T143 [P] Performance optimization: add database indexes if needed for timecard queries on spms_process_data
- [ ] T144 [P] Add i18n translations for all timecard UI text in `frontend/src/i18n/`
- [ ] T145 [P] Add accessibility features (ARIA labels, keyboard navigation) to timecard components
- [ ] T146 [P] Code cleanup and refactoring: remove unused code, optimize component structure
- [ ] T147 [P] Documentation updates: API documentation, component usage guides
- [ ] T148 [P] Security hardening: verify authorization checks on all timecard endpoints
- [ ] T149 [P] Run quickstart.md validation (if exists) to ensure feature can be set up and tested

---

## Dependencies & Execution Order

### Phase Dependencies

- **Setup (Phase 1)**: No dependencies - can start immediately
- **Foundational (Phase 2)**: Depends on Setup completion - BLOCKS all user stories
- **User Stories (Phase 3-9)**: All depend on Foundational phase completion
  - User Story 0 (Phase 3): Must complete before User Story 1
  - User Story 1 (Phase 4): Depends on User Story 0, can proceed independently after
  - User Story 2 (Phase 5): Depends on User Story 1 Part 3 (submission), can proceed after Phase 4 Part 3
  - User Story 4 (Phase 6): Can start after User Story 1 Part 2 (project validation), minimal dependency
  - User Story 5 (Phase 7): Can start after User Story 1 Part 2 (holiday highlighting), minimal dependency
  - User Story 6 (Phase 8): Depends on User Story 1 Part 3 (submission) and User Story 2 (approval)
  - User Story 3 (Phase 9): Can start independently, organization module exists
- **Polish (Phase 10)**: Depends on all desired user stories being complete

### User Story Dependencies

- **User Story 0 (P1)**: Can start after Foundational (Phase 2) - No dependencies on other stories
- **User Story 1 (P1)**: Can start after User Story 0 - Core functionality, enables all other stories
- **User Story 2 (P1)**: Can start after User Story 1 Part 3 (submission) - Depends on timecard submission working
- **User Story 4 (P2)**: Can start after User Story 1 Part 2 (project validation) - Minimal dependency
- **User Story 5 (P2)**: Can start after User Story 1 Part 2 (holiday highlighting) - Minimal dependency
- **User Story 6 (P3)**: Can start after User Story 1 Part 3 and User Story 2 - Needs approved timecards
- **User Story 3 (P2)**: Can start independently - Organization module exists, verify/extend as needed

### Within Each User Story

- Tests (if included) MUST be written and FAIL before implementation
- DTOs before services
- Services before controllers
- Backend APIs before frontend integration
- Core implementation before integration
- Story complete before moving to next priority

### Parallel Opportunities

- All Setup tasks marked [P] can run in parallel
- All Foundational tasks marked [P] can run in parallel (within Phase 2)
- Once Foundational phase completes:
  - User Story 0 can start (blocks User Story 1)
  - After User Story 0: User Story 1 can start
  - After User Story 1 Part 2: User Stories 4 and 5 can start in parallel
  - After User Story 1 Part 3: User Story 2 can start
  - User Story 3 can start independently
  - After User Story 1 Part 3 + User Story 2: User Story 6 can start
- All tests for a user story marked [P] can run in parallel
- DTOs within a story marked [P] can run in parallel
- Different user stories can be worked on in parallel by different team members (respecting dependencies)

---

## Implementation Strategy

### MVP First (User Stories 0, 1, 2 Only)

1. Complete Phase 1: Setup
2. Complete Phase 2: Foundational (CRITICAL - blocks all stories)
3. Complete Phase 3: User Story 0 (Form Registration)
4. Complete Phase 4: User Story 1 (Timecard Entry)
5. Complete Phase 5: User Story 2 (Approval Workflow)
6. **STOP and VALIDATE**: Test complete MVP workflow independently
7. Deploy/demo if ready

### Incremental Delivery

1. Complete Setup + Foundational → Foundation ready
2. Add User Story 0 → Test independently → Form registered
3. Add User Story 1 → Test independently → Employees can submit timecards
4. Add User Story 2 → Test independently → Managers can approve → Deploy/Demo (MVP!)
5. Add User Story 4 → Test independently → Project management enhanced
6. Add User Story 5 → Test independently → Holiday management functional
7. Add User Story 6 → Test independently → Reports available
8. Add User Story 3 → Test independently → Organization management complete
9. Each story adds value without breaking previous stories

### Parallel Team Strategy

With multiple developers:

1. Team completes Setup + Foundational together
2. Once Foundational is done:
   - Developer A: User Story 0 → User Story 1
   - Developer B: User Story 3 (can start independently)
3. Once User Story 1 Part 2 is done:
   - Developer A: User Story 1 Part 3 → User Story 2
   - Developer B: User Story 4 (Project Management)
   - Developer C: User Story 5 (Holiday Management)
4. Once User Story 1 Part 3 + User Story 2 are done:
   - Developer A: User Story 6 (Reports)
   - Developer B: Polish & optimization
5. Stories complete and integrate independently

---

## Notes

- [P] tasks = different files, no dependencies - can run in parallel
- [Story] label maps task to specific user story for traceability
- Each user story should be independently completable and testable
- Verify tests fail before implementing
- Commit after each task or logical group
- Stop at any checkpoint to validate story independently
- Avoid: vague tasks, same file conflicts, cross-story dependencies that break independence
- Organization module already exists - User Story 3 tasks focus on verification and extension
- Flowable BPMN engine already exists - integration tasks focus on using existing APIs
- Process-Form Management already exists - User Story 0 tasks focus on registration only

