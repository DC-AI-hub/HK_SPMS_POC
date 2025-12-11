package com.spms.backend.service.timecard;

import com.spms.backend.controller.dto.timecard.HolidayDTO;
import java.util.List;

/**
 * 节假日服务接口
 * 提供节假日查询和CRUD功能
 */
public interface HolidayService {
    
    /**
     * 获取节假日日期列表
     * 支持按年份、月份筛选
     * 
     * @param year 年份（可选）
     * @param month 月份 1-12（可选）
     * @return 节假日日期列表
     */
    List<HolidayDTO> getHolidays(Integer year, Integer month);
    
    /**
     * 创建节假日
     * 
     * @param holidayDTO 节假日数据
     * @return 创建的节假日DTO
     * @throws IllegalArgumentException 如果日期和国家组合已存在
     */
    HolidayDTO createHoliday(HolidayDTO holidayDTO);
    
    /**
     * 更新节假日
     * 
     * @param id 节假日ID
     * @param holidayDTO 更新的节假日数据
     * @return 更新后的节假日DTO
     * @throws jakarta.persistence.EntityNotFoundException 如果节假日不存在
     */
    HolidayDTO updateHoliday(Long id, HolidayDTO holidayDTO);
    
    /**
     * 删除节假日（软删除）
     * 
     * @param id 节假日ID
     * @throws jakarta.persistence.EntityNotFoundException 如果节假日不存在
     */
    void deleteHoliday(Long id);
}

