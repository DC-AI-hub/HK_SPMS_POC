package com.spms.backend.controller.timecard;

import com.spms.backend.controller.dto.timecard.ImportResultDTO;
import com.spms.backend.service.timecard.ExcelImportExportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayOutputStream;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

/**
 * Excel 导入导出控制器
 * 提供项目、任务、节假日的 Excel 导入导出 REST API
 */
@RestController
@RequestMapping("/api/v1/timecard")
public class ExcelController {
    
    private final ExcelImportExportService excelService;
    
    @Autowired
    public ExcelController(ExcelImportExportService excelService) {
        this.excelService = excelService;
    }
    
    /**
     * 导入项目数据
     * Excel 格式：projectCode | projectName | taskNumber | taskName | activity | status
     * 
     * @param file Excel 文件
     * @return 导入结果
     */
    @PostMapping("/projects/import")
    public ResponseEntity<ImportResultDTO> importProjects(
            @RequestParam("file") MultipartFile file) {
        
        // 验证文件
        if (file.isEmpty()) {
            return ResponseEntity.badRequest().build();
        }
        
        ImportResultDTO result = excelService.importProjects(file);
        return ResponseEntity.ok(result);
    }
    
    /**
     * 导出项目数据
     * 
     * @param status 项目状态筛选（可选）
     * @return Excel 文件
     */
    @GetMapping("/projects/export")
    public ResponseEntity<byte[]> exportProjects(
            @RequestParam(required = false) String status) {
        
        ByteArrayOutputStream outputStream = excelService.exportProjects(status);
        
        String filename = "projects_export_" + 
                LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd")) + ".xlsx";
        
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.parseMediaType(
                "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"));
        headers.setContentDisposition(ContentDisposition.builder("attachment")
                .filename(filename).build());
        
        return new ResponseEntity<>(outputStream.toByteArray(), headers, HttpStatus.OK);
    }
    
    /**
     * 导入节假日数据
     * Excel 格式：date
     * 
     * @param file Excel 文件
     * @return 导入结果
     */
    @PostMapping("/holidays/import")
    public ResponseEntity<ImportResultDTO> importHolidays(
            @RequestParam("file") MultipartFile file) {
        
        // 验证文件
        if (file.isEmpty()) {
            return ResponseEntity.badRequest().build();
        }
        
        ImportResultDTO result = excelService.importHolidays(file);
        return ResponseEntity.ok(result);
    }
    
    /**
     * 导出节假日数据
     * 
     * @param year 年份筛选（可选）
     * @return Excel 文件
     */
    @GetMapping("/holidays/export")
    public ResponseEntity<byte[]> exportHolidays(
            @RequestParam(required = false) Integer year) {
        
        ByteArrayOutputStream outputStream = excelService.exportHolidays(year);
        
        String filename = "holidays_export_" + 
                LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd")) + ".xlsx";
        
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.parseMediaType(
                "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"));
        headers.setContentDisposition(ContentDisposition.builder("attachment")
                .filename(filename).build());
        
        return new ResponseEntity<>(outputStream.toByteArray(), headers, HttpStatus.OK);
    }
}

