package com.spms.backend.service.timecard;

import com.spms.backend.controller.dto.timecard.ImportResultDTO;
import org.springframework.web.multipart.MultipartFile;
import java.io.ByteArrayOutputStream;

/**
 * Excel 导入导出服务接口
 * 提供项目、任务、节假日的 Excel 导入导出功能
 */
public interface ExcelImportExportService {
    
    /**
     * 导入项目数据
     * Excel 格式：projectCode | projectName | taskNumber | taskName | activity | status
     * 
     * @param file Excel 文件
     * @return 导入结果 DTO
     */
    ImportResultDTO importProjects(MultipartFile file);
    
    /**
     * 导出项目数据
     * 
     * @param status 项目状态筛选（可选）
     * @return Excel 文件输出流
     */
    ByteArrayOutputStream exportProjects(String status);
    
    /**
     * 导入节假日数据
     * Excel 格式：date
     * 
     * @param file Excel 文件
     * @return 导入结果 DTO
     */
    ImportResultDTO importHolidays(MultipartFile file);
    
    /**
     * 导出节假日数据
     * 
     * @param year 年份筛选（可选）
     * @return Excel 文件输出流
     */
    ByteArrayOutputStream exportHolidays(Integer year);
}

