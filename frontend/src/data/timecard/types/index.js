// Type definitions for Timecard form
// Aligned with SPMS project patterns and TypeScript conventions

export const ClaimType = {
  NORMAL: "NORMAL",
  LEAVE: "LEAVE",
  OT_WORKING_DAYS: "OT_WORKING_DAYS",
  OT_HOLIDAYS: "OT_HOLIDAYS",
  OT_STATUTORY_HOLIDAYS: "OT_STATUTORY_HOLIDAYS"
};

export const ProjectStatus = {
  ACTIVE: "ACTIVE",
  COMPLETED: "COMPLETED"
};

export const TimecardStatus = {
  DRAFT: "DRAFT",
  SUBMITTED: "SUBMITTED",
  APPROVAL_PENDING: "APPROVAL_PENDING",
  APPROVED: "APPROVED",
  REJECTED: "REJECTED"
};

/**
 * @typedef {Object} StaffInfo
 * @property {string} staffId
 * @property {string} staffNameChinese
 * @property {string} staffNameEnglish
 * @property {string} team
 * @property {string} staffType
 * @property {string} departmentHead
 * @property {string} finalApproval
 * @property {string} timecardMonth
 */

/**
 * @typedef {Object} Project
 * @property {string} id
 * @property {string} projectCode
 * @property {string} projectName
 * @property {string} taskNumber
 * @property {string} activity
 * @property {string} status
 */

/**
 * @typedef {Object} Holiday
 * @property {string} id
 * @property {string} date
 * @property {string} country
 * @property {string} name
 * @property {string} type
 */

/**
 * @typedef {Object} TimecardEntry
 * @property {string} id
 * @property {string} date
 * @property {string} projectCode
 * @property {string} projectName
 * @property {string} taskNumber
 * @property {string} activity
 * @property {string} claimType
 * @property {number} hours
 * @property {string} remark
 */

/**
 * @typedef {Object} TimecardSummary
 * @property {string} projectCode
 * @property {string} projectName
 * @property {string} taskNumber
 * @property {string} activity
 * @property {number} totalHours
 */

/**
 * @typedef {Object} ProjectEntryForm
 * @property {string} projectCode
 * @property {string} projectName
 * @property {string} taskNumber
 * @property {string} activity
 * @property {string} claimType
 * @property {string} hours
 * @property {string} remark
 */

// Export types for JSDoc usage
export const TimecardTypes = {
  StaffInfo: {},
  Project: {},
  Holiday: {},
  TimecardEntry: {},
  TimecardSummary: {},
  ProjectEntryForm: {}
};