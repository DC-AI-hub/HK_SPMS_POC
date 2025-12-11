package com.spms.backend.service.project;

import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

/**
 * 项目数据迁移服务接口
 * 用于处理生产环境数据导入和字段映射
 */
public interface ProjectDataMigrationService {

    /**
     * 分析Excel文件结构
     * @param file Excel文件
     * @return 字段分析结果
     */
    Map<String, Object> analyzeExcelStructure(MultipartFile file);

    /**
     * 预览数据导入结果
     * @param file Excel文件
     * @param fieldMapping 字段映射配置
     * @return 预览数据
     */
    List<Map<String, Object>> previewImport(MultipartFile file, Map<String, String> fieldMapping);

    /**
     * 执行数据导入
     * @param file Excel文件
     * @param fieldMapping 字段映射配置
     * @param validationRules 验证规则
     * @return 导入结果
     */
    ImportResult importData(MultipartFile file, Map<String, String> fieldMapping, Map<String, Object> validationRules);

    /**
     * 字段映射建议
     * 基于Excel列名智能推荐映射到实体字段
     * @param excelHeaders Excel列头
     * @return 映射建议
     */
    Map<String, String> suggestFieldMapping(List<String> excelHeaders);

    /**
     * 数据清洗和标准化
     * @param rawData 原始数据
     * @return 清洗后的数据
     */
    Map<String, Object> cleanseData(Map<String, Object> rawData);

    /**
     * 导入结果记录
     */
    record ImportResult(
        int totalRows,
        int successRows,
        int failedRows,
        List<String> errors,
        List<String> warnings
    ) {}
}
