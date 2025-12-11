package com.spms.backend.controller.dto.timecard;

import lombok.Data;
import java.time.LocalDate;

/**
 * 节假日 DTO
 * 用于 Timecard 表单中标识节假日日期
 */
@Data
public class HolidayDTO {
    
    /**
     * 节假日 ID
     */
    private Long id;
    
    /**
     * 节假日日期（格式：YYYY-MM-DD）
     */
    private LocalDate date;
    
    /**
     * 国家代码（CN/HK/US/UK）
     */
    private String country;
    
    /**
     * 节假日名称
     */
    private String name;
    
    /**
     * 节假日类型（PUBLIC_HOLIDAY/COMPANY_HOLIDAY）
     */
    private String type;
}

