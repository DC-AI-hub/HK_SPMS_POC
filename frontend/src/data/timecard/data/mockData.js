// Static configuration for Timecard form
// These are UI constants and not replaced by backend API

export const CLAIM_TYPE_OPTIONS = [
  { value: "NORMAL", label: "Normal" },
  { value: "LEAVE", label: "Leave" },
  { value: "OT_WORKING_DAYS", label: "OT - Working Days" },
  { value: "OT_HOLIDAYS", label: "OT - Holidays" },
  { value: "OT_STATUTORY_HOLIDAYS", label: "OT - Statutory Holidays" }
];

export const PROJECT_COLORS = [
  "#E3F2FD", // blue-100
  "#F3E5F5", // purple-100
  "#E8F5E8", // green-100
  "#FFFDE7", // yellow-100
  "#FCE4EC", // pink-100
  "#E8EAF6", // indigo-100
  "#FFF3E0", // orange-100
  "#E0F2F1"  // teal-100
];

export const PROJECT_COLORS_BORDER = [
  "#90CAF9", // blue-300
  "#CE93D8", // purple-300
  "#A5D6A7", // green-300
  "#FFF59D", // yellow-300
  "#F48FB1", // pink-300
  "#9FA8DA", // indigo-300
  "#FFCC80", // orange-300
  "#80CBC4"  // teal-300
];

// Initial empty timecard entries
export const initialTimecardEntries = [];

// Sample timecard entries for testing
export const sampleTimecardEntries = [
  {
    id: "entry-1",
    date: "2025-11-03",
    projectCode: "PROJ001",
    projectName: "System Upgrade",
    taskNumber: "T001",
    activity: "Development",
    claimType: "NORMAL",
    hours: 8,
    remark: "Daily development work"
  },
  {
    id: "entry-2",
    date: "2025-11-03",
    projectCode: "PROJ002",
    projectName: "Mobile App",
    taskNumber: "T002",
    activity: "Testing",
    claimType: "NORMAL",
    hours: 2,
    remark: "Mobile app testing"
  },
  {
    id: "entry-3",
    date: "2025-11-04",
    projectCode: "PROJ001",
    projectName: "System Upgrade",
    taskNumber: "T001",
    activity: "Development",
    claimType: "NORMAL",
    hours: 8,
    remark: ""
  },
  {
    id: "entry-4",
    date: "2025-11-05",
    projectCode: "PROJ001",
    projectName: "System Upgrade",
    taskNumber: "T001",
    activity: "Development",
    claimType: "OT_WORKING_DAYS",
    hours: 3,
    remark: "Overtime for urgent bug fix"
  }
];

// Mock data for Reports

// TimecardSummaryReport Mock Data (年度工时汇总报表)
export const mockSummaryReportData = [
  {
    projectCode: "PROJ001",
    task: "T001",
    activity: "Development",
    claimType: "NORMAL",
    staffType: "Permanent",
    ytdManHours: 1280
  },
  {
    projectCode: "PROJ001",
    task: "T001",
    activity: "Development",
    claimType: "OT_WORKING_DAYS",
    staffType: "Permanent",
    ytdManHours: 96
  },
  {
    projectCode: "PROJ002",
    task: "T002",
    activity: "Testing",
    claimType: "NORMAL",
    staffType: "Vendor",
    ytdManHours: 960
  },
  {
    projectCode: "CC-001",
    task: "T003",
    activity: "Support",
    claimType: "NORMAL",
    staffType: "Permanent",
    ytdManHours: 640
  }
];

// IndividualTimecardReport Mock Data (个人工时汇总报表)
export const mockIndividualReportData = [
  {
    formId: "TC-2025-11-001",
    timecardStatus: "APPROVED",
    submissionTime: "2025-11-15T10:30:00",
    finalApprovalTime: "2025-11-16T14:20:00",
    staffId: "EMP001",
    staffNameChinese: "张伟",
    staffName: "Wei Zhang",
    staffType: "Permanent",
    timecardMonth: "2025-11",
    supervisor: "John Smith",
    finalApprover: "Sarah Johnson"
  },
  {
    formId: "TC-2025-10-001",
    timecardStatus: "APPROVED",
    submissionTime: "2025-10-15T10:30:00",
    finalApprovalTime: "2025-10-16T14:20:00",
    staffId: "EMP001",
    staffNameChinese: "张伟",
    staffName: "Wei Zhang",
    staffType: "Permanent",
    timecardMonth: "2025-10",
    supervisor: "John Smith",
    finalApprover: "Sarah Johnson"
  }
];

// TimecardDetailsReport Mock Data (工时明细报表)
export const mockDetailsReportData = [
  {
    formId: "TC-2025-11-001",
    staffId: "EMP001",
    staffNameChinese: "张伟",
    staffName: "Wei Zhang",
    team: "IT Development",
    timecardMonth: "2025-11",
    codeType: "Project Code",
    projectCode: "PROJ001",
    projectName: "System Upgrade",
    task: "T001",
    activity: "Development",
    claimType: "NORMAL",
    adjustmentMonth: null,
    department: "IT",
    chargeGrade: "A",
    staffType: "Permanent",
    supervisor: "John Smith",
    finalApprover: "Sarah Johnson",
    manHours: 160,
    submissionTime: "2025-11-15T10:30:00",
    timecardStatus: "APPROVED",
    finalApprovalTime: "2025-11-16T14:20:00"
  },
  {
    formId: "TC-2025-11-002",
    staffId: "EMP002",
    staffNameChinese: "李娜",
    staffName: "Li Na",
    team: "IT Development",
    timecardMonth: "2025-11",
    codeType: "Charge Code",
    projectCode: "CC-001",
    projectName: "Support",
    task: "T003",
    activity: "Support",
    claimType: "OT_WORKING_DAYS",
    adjustmentMonth: null,
    department: "IT",
    chargeGrade: "B",
    staffType: "Permanent",
    supervisor: "John Smith",
    finalApprover: "Sarah Johnson",
    manHours: 168,
    submissionTime: "2025-11-15T11:00:00",
    timecardStatus: "APPROVED",
    finalApprovalTime: "2025-11-16T15:00:00"
  }
];

// PermStaffOTReport Mock Data (正式员工加班记录报表)
export const mockOTReportData = [
  {
    formId: "TC-2025-11-001",
    submissionTime: "2025-11-15T10:30:00",
    timecardStatus: "APPROVED",
    finalApprovalTime: "2025-11-16T14:20:00",
    staffId: "EMP001",
    staffNameChinese: "张伟",
    staffName: "Wei Zhang",
    team: "IT Development",
    timecardMonth: "2025-11",
    projectCode: "PROJ001",
    adjustmentMonth: null,
    claimType: "OT_WORKING_DAYS",
    staffType: "Permanent",
    supervisor: "John Smith",
    manHours: 8
  },
  {
    formId: "TC-2025-11-002",
    submissionTime: "2025-11-15T11:00:00",
    timecardStatus: "APPROVED",
    finalApprovalTime: "2025-11-16T15:00:00",
    staffId: "EMP002",
    staffNameChinese: "李娜",
    staffName: "Li Na",
    team: "IT Development",
    timecardMonth: "2025-11",
    projectCode: "PROJ002",
    adjustmentMonth: null,
    claimType: "OT_HOLIDAYS",
    staffType: "Permanent",
    supervisor: "John Smith",
    manHours: 12
  },
  {
    formId: "TC-2025-11-003",
    submissionTime: "2025-11-15T11:30:00",
    timecardStatus: "APPROVED",
    finalApprovalTime: "2025-11-16T15:30:00",
    staffId: "EMP003",
    staffNameChinese: "陈明",
    staffName: "Chen Ming",
    team: "IT Development",
    timecardMonth: "2025-11",
    projectCode: "PROJ003",
    adjustmentMonth: null,
    claimType: "OT_WORKING_DAYS",
    staffType: "Vendor",
    supervisor: "John Smith",
    manHours: 6
  }
];