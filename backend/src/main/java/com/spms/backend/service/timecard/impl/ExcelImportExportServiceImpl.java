package com.spms.backend.service.timecard.impl;

import com.spms.backend.controller.dto.timecard.ImportResultDTO;
import com.spms.backend.model.ProjectStatus;
import com.spms.backend.repository.entities.project.ProjectEntity;
import com.spms.backend.repository.entities.project.TaskEntity;
import com.spms.backend.repository.entities.timecard.HolidayEntity;
import com.spms.backend.repository.project.ProjectRepository;
import com.spms.backend.repository.project.TaskRepository;
import com.spms.backend.repository.timecard.HolidayRepository;
import com.spms.backend.service.idm.UserService;
import com.spms.backend.service.timecard.ExcelImportExportService;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Excel 导入导出服务实现类
 * 使用 Apache POI 处理 Excel 文件
 */
@Service
public class ExcelImportExportServiceImpl implements ExcelImportExportService {
    
    private final ProjectRepository projectRepository;
    private final TaskRepository taskRepository;
    private final HolidayRepository holidayRepository;
    private final UserService userService;
    
    @Autowired
    public ExcelImportExportServiceImpl(
            ProjectRepository projectRepository,
            TaskRepository taskRepository,
            HolidayRepository holidayRepository,
            UserService userService) {
        this.projectRepository = projectRepository;
        this.taskRepository = taskRepository;
        this.holidayRepository = holidayRepository;
        this.userService = userService;
    }
    
    /**
     * 导入项目数据
     * Excel 格式：projectCode | projectName | taskNumber | taskName | activity | status
     */
    @Override
    @Transactional
    public ImportResultDTO importProjects(MultipartFile file) {
        ImportResultDTO result = new ImportResultDTO();
        result.setSuccess(true);
        int imported = 0;
        int failed = 0;
        
        try (Workbook workbook = new XSSFWorkbook(file.getInputStream())) {
            Sheet sheet = workbook.getSheetAt(0);
            
            // 跳过标题行，从第二行开始
            for (int i = 1; i <= sheet.getLastRowNum(); i++) {
                Row row = sheet.getRow(i);
                if (row == null) continue;
                
                try {
                    // 读取单元格数据
                    String projectCode = getCellValueAsString(row.getCell(0));
                    String projectName = getCellValueAsString(row.getCell(1));
                    String taskNumber = getCellValueAsString(row.getCell(2));
                    String taskName = getCellValueAsString(row.getCell(3));
                    String activity = getCellValueAsString(row.getCell(4));
                    String status = getCellValueAsString(row.getCell(5));
                    
                    // 验证必填字段
                    if (projectCode == null || projectCode.trim().isEmpty()) {
                        addError(result, i + 1, projectCode, "项目代码不能为空");
                        failed++;
                        continue;
                    }
                    
                    // 查找或创建项目
                    ProjectEntity project = projectRepository.findByProjectCode(projectCode)
                            .orElseGet(() -> {
                                ProjectEntity newProject = new ProjectEntity();
                                newProject.setProjectCode(projectCode);
                                newProject.setProjectName(projectName != null ? projectName : projectCode);
                                newProject.setStatus(parseProjectStatus(status));
                                newProject.setIsActive(true);
                                return projectRepository.save(newProject);
                            });
                    
                    // 如果有任务信息，创建或更新任务
                    if (taskNumber != null && !taskNumber.trim().isEmpty()) {
                        TaskEntity task = taskRepository.findByTaskNumber(taskNumber)
                                .orElseGet(() -> {
                                    TaskEntity newTask = new TaskEntity();
                                    newTask.setTaskNumber(taskNumber);
                                    newTask.setTaskName(taskName != null ? taskName : taskNumber);
                                    newTask.setActivity(activity);
                                    newTask.setProject(project);
                                    newTask.setIsActive(true);
                                    return taskRepository.save(newTask);
                                });
                        
                        // 更新任务信息（如果已存在）
                        if (task.getId() != null) {
                            if (taskName != null) task.setTaskName(taskName);
                            if (activity != null) task.setActivity(activity);
                            taskRepository.save(task);
                        }
                    }
                    
                    imported++;
                } catch (Exception e) {
                    addError(result, i + 1, "N/A", "处理错误: " + e.getMessage());
                    failed++;
                }
            }
            
            result.setImported(imported);
            result.setFailed(failed);
            
        } catch (IOException e) {
            result.setSuccess(false);
            addError(result, 0, "N/A", "读取 Excel 文件失败: " + e.getMessage());
        }
        
        return result;
    }
    
    /**
     * 导出项目数据
     */
    @Override
    @Transactional(readOnly = true)
    public ByteArrayOutputStream exportProjects(String status) {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        
        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("Projects");
            
            // 创建标题行
            Row headerRow = sheet.createRow(0);
            String[] headers = {"Project Code", "Project Name", "Task Number", "Task Name", "Activity", "Status"};
            for (int i = 0; i < headers.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(headers[i]);
                
                // 设置标题样式
                CellStyle headerStyle = workbook.createCellStyle();
                Font font = workbook.createFont();
                font.setBold(true);
                headerStyle.setFont(font);
                cell.setCellStyle(headerStyle);
            }
            
            // 查询项目数据（只查询激活的项目）
            List<ProjectEntity> projects;
            if (status != null && !status.trim().isEmpty() && !status.equals("ALL")) {
                ProjectStatus projectStatus = parseProjectStatus(status);
                projects = projectRepository.findByStatus(projectStatus).stream()
                        .filter(ProjectEntity::getIsActive)
                        .collect(java.util.stream.Collectors.toList());
            } else {
                projects = projectRepository.findAll().stream()
                        .filter(ProjectEntity::getIsActive)
                        .collect(java.util.stream.Collectors.toList());
            }
            
            // 填充数据行
            int rowNum = 1;
            for (ProjectEntity project : projects) {
                // 只查询激活的任务
                List<TaskEntity> tasks = taskRepository.findByProjectIdAndIsActiveTrue(project.getId());
                
                if (tasks.isEmpty()) {
                    // 如果项目没有任务，只输出项目信息
                    Row row = sheet.createRow(rowNum++);
                    row.createCell(0).setCellValue(project.getProjectCode());
                    row.createCell(1).setCellValue(project.getProjectName());
                    row.createCell(2).setCellValue("");
                    row.createCell(3).setCellValue("");
                    row.createCell(4).setCellValue("");
                    row.createCell(5).setCellValue(project.getStatus().name());
                } else {
                    // 为每个任务创建一行
                    for (TaskEntity task : tasks) {
                        Row row = sheet.createRow(rowNum++);
                        row.createCell(0).setCellValue(project.getProjectCode());
                        row.createCell(1).setCellValue(project.getProjectName());
                        row.createCell(2).setCellValue(task.getTaskNumber());
                        row.createCell(3).setCellValue(task.getTaskName());
                        row.createCell(4).setCellValue(task.getActivity() != null ? task.getActivity() : "");
                        row.createCell(5).setCellValue(project.getStatus().name());
                    }
                }
            }
            
            // 自动调整列宽
            for (int i = 0; i < headers.length; i++) {
                sheet.autoSizeColumn(i);
            }
            
            workbook.write(outputStream);
            
        } catch (IOException e) {
            throw new RuntimeException("导出 Excel 失败: " + e.getMessage(), e);
        }
        
        return outputStream;
    }
    
    /**
     * 导入节假日数据
     * Excel 格式：date
     */
    @Override
    @Transactional
    public ImportResultDTO importHolidays(MultipartFile file) {
        ImportResultDTO result = new ImportResultDTO();
        result.setSuccess(true);
        int imported = 0;
        int failed = 0;
        
        try (Workbook workbook = new XSSFWorkbook(file.getInputStream())) {
            Sheet sheet = workbook.getSheetAt(0);
            Long currentUserId = userService.getCurrentUserId();
            long timestamp = System.currentTimeMillis();
            
            // 跳过标题行，从第二行开始
            for (int i = 1; i <= sheet.getLastRowNum(); i++) {
                Row row = sheet.getRow(i);
                if (row == null) continue;
                
                try {
                    // 读取日期
                    Cell dateCell = row.getCell(0);
                    LocalDate date = getCellValueAsDate(dateCell);
                    
                    if (date == null) {
                        addError(result, i + 1, "N/A", "日期格式不正确");
                        failed++;
                        continue;
                    }
                    
                    // 检查是否已存在
                    if (holidayRepository.existsByHolidayDate(date)) {
                        addError(result, i + 1, date.toString(), "节假日已存在");
                        failed++;
                        continue;
                    }
                    
                    // 创建节假日记录
                    HolidayEntity holiday = new HolidayEntity();
                    holiday.setHolidayDate(date);
                    holiday.setCreatedAt(timestamp);
                    holiday.setUpdatedAt(timestamp);
                    holiday.setCreatedBy(currentUserId);
                    holiday.setUpdatedBy(currentUserId);
                    
                    holidayRepository.save(holiday);
                    imported++;
                    
                } catch (Exception e) {
                    addError(result, i + 1, "N/A", "处理错误: " + e.getMessage());
                    failed++;
                }
            }
            
            result.setImported(imported);
            result.setFailed(failed);
            
        } catch (IOException e) {
            result.setSuccess(false);
            addError(result, 0, "N/A", "读取 Excel 文件失败: " + e.getMessage());
        }
        
        return result;
    }
    
    /**
     * 导出节假日数据
     */
    @Override
    @Transactional(readOnly = true)
    public ByteArrayOutputStream exportHolidays(Integer year) {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        
        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("Holidays");
            
            // 创建标题行
            Row headerRow = sheet.createRow(0);
            String[] headers = {"Date"};
            for (int i = 0; i < headers.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(headers[i]);
                
                // 设置标题样式
                CellStyle headerStyle = workbook.createCellStyle();
                Font font = workbook.createFont();
                font.setBold(true);
                headerStyle.setFont(font);
                cell.setCellStyle(headerStyle);
            }
            
            // 查询节假日数据
            List<HolidayEntity> holidays;
            if (year != null) {
                holidays = holidayRepository.findByYear(year);
            } else {
                holidays = holidayRepository.findAllByOrderByHolidayDateAsc();
            }
            
            // 创建日期样式
            CellStyle dateStyle = workbook.createCellStyle();
            CreationHelper createHelper = workbook.getCreationHelper();
            dateStyle.setDataFormat(createHelper.createDataFormat().getFormat("yyyy-mm-dd"));
            
            // 填充数据行
            int rowNum = 1;
            for (HolidayEntity holiday : holidays) {
                Row row = sheet.createRow(rowNum++);
                Cell dateCell = row.createCell(0);
                dateCell.setCellValue(Date.from(holiday.getHolidayDate().atStartOfDay(ZoneId.systemDefault()).toInstant()));
                dateCell.setCellStyle(dateStyle);
            }
            
            // 自动调整列宽
            sheet.autoSizeColumn(0);
            
            workbook.write(outputStream);
            
        } catch (IOException e) {
            throw new RuntimeException("导出 Excel 失败: " + e.getMessage(), e);
        }
        
        return outputStream;
    }
    
    // ==================== 私有辅助方法 ====================
    
    /**
     * 获取单元格值为字符串
     */
    private String getCellValueAsString(Cell cell) {
        if (cell == null) return null;
        
        return switch (cell.getCellType()) {
            case STRING -> cell.getStringCellValue();
            case NUMERIC -> String.valueOf((long) cell.getNumericCellValue());
            case BOOLEAN -> String.valueOf(cell.getBooleanCellValue());
            case FORMULA -> cell.getCellFormula();
            default -> null;
        };
    }
    
    /**
     * 获取单元格值为日期
     */
    private LocalDate getCellValueAsDate(Cell cell) {
        if (cell == null) return null;
        
        try {
            if (cell.getCellType() == CellType.NUMERIC && DateUtil.isCellDateFormatted(cell)) {
                Date date = cell.getDateCellValue();
                return date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
            } else if (cell.getCellType() == CellType.STRING) {
                return LocalDate.parse(cell.getStringCellValue());
            }
        } catch (Exception e) {
            // 解析失败，返回 null
        }
        
        return null;
    }
    
    /**
     * 解析项目状态
     */
    private ProjectStatus parseProjectStatus(String status) {
        if (status == null || status.trim().isEmpty()) {
            return ProjectStatus.ACTIVE;
        }
        
        try {
            return ProjectStatus.valueOf(status.toUpperCase());
        } catch (IllegalArgumentException e) {
            return ProjectStatus.ACTIVE;
        }
    }
    
    /**
     * 添加导入错误信息
     */
    private void addError(ImportResultDTO result, int row, String identifier, String error) {
        ImportResultDTO.ImportErrorDTO errorDTO = new ImportResultDTO.ImportErrorDTO();
        errorDTO.setRow(row);
        errorDTO.setIdentifier(identifier);
        errorDTO.setError(error);
        
        if (result.getErrors() == null) {
            result.setErrors(new ArrayList<>());
        }
        result.getErrors().add(errorDTO);
    }
}

