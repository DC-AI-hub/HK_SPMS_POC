package com.spms.backend.service.timecard.impl;

import com.spms.backend.controller.dto.timecard.HolidayDTO;
import com.spms.backend.repository.entities.timecard.HolidayEntity;
import com.spms.backend.repository.timecard.HolidayRepository;
import com.spms.backend.service.idm.UserService;
import com.spms.backend.service.timecard.HolidayService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 节假日服务实现类
 * 提供节假日查询和CRUD功能
 */
@Service
public class HolidayServiceImpl implements HolidayService {
    
    private final HolidayRepository holidayRepository;
    private final UserService userService;
    
    @Autowired
    public HolidayServiceImpl(HolidayRepository holidayRepository, UserService userService) {
        this.holidayRepository = holidayRepository;
        this.userService = userService;
    }
    
    /**
     * 获取节假日日期列表
     * 
     * @param year 年份（可选）
     * @param month 月份（可选）
     * @return 节假日日期列表
     */
    @Override
    @Transactional(readOnly = true)
    public List<HolidayDTO> getHolidays(Integer year, Integer month) {
        List<HolidayEntity> holidays;
        
        // 根据参数选择查询方式（移除isActive判断，查询所有holiday）
        if (year != null && month != null) {
            // 查询指定年月的节假日
            holidays = holidayRepository.findByYearAndMonth(year, month);
        } else if (year != null) {
            // 查询指定年份的节假日
            holidays = holidayRepository.findByYear(year);
        } else {
            // 查询所有节假日
            holidays = holidayRepository.findAllByOrderByHolidayDateAsc();
        }
        
        // 转换为 DTO
        return holidays.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
    
    /**
     * 创建节假日
     * 
     * @param holidayDTO 节假日数据
     * @return 创建的节假日DTO
     * @throws IllegalArgumentException 如果日期和国家组合已存在
     */
    @Override
    @Transactional
    public HolidayDTO createHoliday(HolidayDTO holidayDTO) {
        // 验证必填字段
        if (holidayDTO.getDate() == null) {
            throw new IllegalArgumentException("节假日日期不能为空");
        }
        if (holidayDTO.getCountry() == null || holidayDTO.getCountry().trim().isEmpty()) {
            throw new IllegalArgumentException("国家代码不能为空");
        }
        if (holidayDTO.getName() == null || holidayDTO.getName().trim().isEmpty()) {
            throw new IllegalArgumentException("节假日名称不能为空");
        }
        if (holidayDTO.getType() == null || holidayDTO.getType().trim().isEmpty()) {
            throw new IllegalArgumentException("节假日类型不能为空");
        }
        
        // 检查同一日期、同一国家是否已存在节假日
        if (holidayRepository.existsByHolidayDateAndCountry(
                holidayDTO.getDate(), holidayDTO.getCountry())) {
            throw new IllegalArgumentException(
                    String.format("日期 %s 在国家 %s 的节假日已存在", 
                            holidayDTO.getDate(), holidayDTO.getCountry()));
        }
        
        // 创建实体
        HolidayEntity entity = new HolidayEntity();
        entity.setHolidayDate(holidayDTO.getDate());
        entity.setCountry(holidayDTO.getCountry().trim());
        entity.setName(holidayDTO.getName().trim());
        entity.setType(holidayDTO.getType().trim());
        // 移除isActive设置，使用默认值
        
        // 设置审计字段
        Long currentUserId = userService.getCurrentUserId();
        long timestamp = System.currentTimeMillis();
        entity.setCreatedAt(timestamp);
        entity.setUpdatedAt(timestamp);
        entity.setCreatedBy(currentUserId);
        entity.setUpdatedBy(currentUserId);
        
        // 保存
        HolidayEntity saved = holidayRepository.save(entity);
        
        // 转换为DTO并返回
        return convertToDTO(saved);
    }
    
    /**
     * 更新节假日
     * 
     * @param id 节假日ID
     * @param holidayDTO 更新的节假日数据
     * @return 更新后的节假日DTO
     * @throws EntityNotFoundException 如果节假日不存在
     */
    @Override
    @Transactional
    public HolidayDTO updateHoliday(Long id, HolidayDTO holidayDTO) {
        // 查找节假日
        HolidayEntity entity = holidayRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("节假日不存在: ID = " + id));
        
        // 如果日期或国家改变，检查是否与现有记录冲突
        if (holidayDTO.getDate() != null && !entity.getHolidayDate().equals(holidayDTO.getDate()) ||
            holidayDTO.getCountry() != null && !entity.getCountry().equals(holidayDTO.getCountry())) {
            LocalDate newDate = holidayDTO.getDate() != null ? holidayDTO.getDate() : entity.getHolidayDate();
            String newCountry = holidayDTO.getCountry() != null ? holidayDTO.getCountry().trim() : entity.getCountry();
            
            if (holidayRepository.existsByHolidayDateAndCountry(newDate, newCountry)) {
                // 检查是否是同一条记录
                HolidayEntity existing = holidayRepository.findByHolidayDateAndCountry(newDate, newCountry)
                        .orElse(null);
                if (existing != null && !existing.getId().equals(id)) {
                    throw new IllegalArgumentException(
                            String.format("日期 %s 在国家 %s 的节假日已存在", newDate, newCountry));
                }
            }
        }
        
        // 更新字段（只更新非空字段）
        if (holidayDTO.getDate() != null) {
            entity.setHolidayDate(holidayDTO.getDate());
        }
        if (holidayDTO.getCountry() != null && !holidayDTO.getCountry().trim().isEmpty()) {
            entity.setCountry(holidayDTO.getCountry().trim());
        }
        if (holidayDTO.getName() != null && !holidayDTO.getName().trim().isEmpty()) {
            entity.setName(holidayDTO.getName().trim());
        }
        if (holidayDTO.getType() != null && !holidayDTO.getType().trim().isEmpty()) {
            entity.setType(holidayDTO.getType().trim());
        }
        
        // 更新审计字段
        entity.setUpdatedAt(System.currentTimeMillis());
        entity.setUpdatedBy(userService.getCurrentUserId());
        
        // 保存
        HolidayEntity saved = holidayRepository.save(entity);
        
        // 转换为DTO并返回
        return convertToDTO(saved);
    }
    
    /**
     * 删除节假日（硬删除）
     * 
     * @param id 节假日ID
     * @throws EntityNotFoundException 如果节假日不存在
     */
    @Override
    @Transactional
    public void deleteHoliday(Long id) {
        // 查找节假日
        HolidayEntity entity = holidayRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("节假日不存在: ID = " + id));
        
        // 硬删除：直接删除记录
        holidayRepository.delete(entity);
    }
    
    /**
     * 将 HolidayEntity 转换为 HolidayDTO
     * 
     * @param entity 节假日实体
     * @return 节假日 DTO
     */
    private HolidayDTO convertToDTO(HolidayEntity entity) {
        HolidayDTO dto = new HolidayDTO();
        dto.setId(entity.getId());
        dto.setDate(entity.getHolidayDate());
        dto.setCountry(entity.getCountry());
        dto.setName(entity.getName());
        dto.setType(entity.getType());
        return dto;
    }
}

