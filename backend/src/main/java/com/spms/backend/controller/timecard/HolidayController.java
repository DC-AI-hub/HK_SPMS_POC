package com.spms.backend.controller.timecard;

import com.spms.backend.controller.dto.ApiResponse;
import com.spms.backend.controller.dto.timecard.HolidayDTO;
import com.spms.backend.service.timecard.HolidayService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 节假日控制器
 * 提供节假日查询和CRUD的 REST API 端点
 */
@RestController
@RequestMapping("/api/v1/timecard/holidays")
public class HolidayController {
    
    private final HolidayService holidayService;
    
    @Autowired
    public HolidayController(HolidayService holidayService) {
        this.holidayService = holidayService;
    }
    
    /**
     * 获取节假日日期列表
     * 支持按年份、月份筛选
     * 
     * @param year 年份（可选）
     * @param month 月份 1-12（可选）
     * @return 节假日日期列表
     */
    @GetMapping
    public ResponseEntity<List<HolidayDTO>> getHolidays(
            @RequestParam(required = false) Integer year,
            @RequestParam(required = false) Integer month) {
        List<HolidayDTO> holidays = holidayService.getHolidays(year, month);
        return ResponseEntity.ok(holidays);
    }
    
    /**
     * 创建节假日
     * 
     * @param holidayDTO 节假日数据
     * @return 创建的节假日DTO
     */
    @PostMapping
    public ResponseEntity<ApiResponse<HolidayDTO>> createHoliday(@RequestBody HolidayDTO holidayDTO) {
        try {
            HolidayDTO created = holidayService.createHoliday(holidayDTO);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(new ApiResponse<>("SUCCESS", "节假日创建成功", created));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest()
                    .body(new ApiResponse<>("ERROR", e.getMessage(), null));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>("ERROR", "创建节假日失败: " + e.getMessage(), null));
        }
    }
    
    /**
     * 更新节假日
     * 
     * @param id 节假日ID
     * @param holidayDTO 更新的节假日数据
     * @return 更新后的节假日DTO
     */
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<HolidayDTO>> updateHoliday(
            @PathVariable Long id,
            @RequestBody HolidayDTO holidayDTO) {
        try {
            HolidayDTO updated = holidayService.updateHoliday(id, holidayDTO);
            return ResponseEntity.ok(new ApiResponse<>("SUCCESS", "节假日更新成功", updated));
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ApiResponse<>("ERROR", e.getMessage(), null));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest()
                    .body(new ApiResponse<>("ERROR", e.getMessage(), null));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>("ERROR", "更新节假日失败: " + e.getMessage(), null));
        }
    }
    
    /**
     * 删除节假日（软删除）
     * 
     * @param id 节假日ID
     * @return 204 No Content
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteHoliday(@PathVariable Long id) {
        try {
            holidayService.deleteHoliday(id);
            return ResponseEntity.noContent().build();
        } catch (EntityNotFoundException e) {
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}

