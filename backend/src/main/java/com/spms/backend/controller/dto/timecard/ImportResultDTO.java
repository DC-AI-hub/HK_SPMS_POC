package com.spms.backend.controller.dto.timecard;

import lombok.Data;
import java.util.ArrayList;
import java.util.List;

/**
 * 导入结果 DTO
 * 用于返回 Excel 导入操作的结果
 */
@Data
public class ImportResultDTO {
    
    /**
     * 导入是否成功（部分失败也返回 true）
     */
    private boolean success;
    
    /**
     * 成功导入的记录数
     */
    private int imported;
    
    /**
     * 失败的记录数
     */
    private int failed;
    
    /**
     * 错误详情列表
     */
    private List<ImportErrorDTO> errors = new ArrayList<>();
    
    /**
     * 导入错误详情内部类
     */
    @Data
    public static class ImportErrorDTO {
        
        /**
         * 出错的行号（从 1 开始，不包括表头）
         */
        private int row;
        
        /**
         * 标识信息（如项目代码、节假日日期等）
         */
        private String identifier;
        
        /**
         * 错误消息
         */
        private String error;
    }
}

