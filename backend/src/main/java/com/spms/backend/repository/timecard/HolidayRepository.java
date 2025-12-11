package com.spms.backend.repository.timecard;

import com.spms.backend.repository.entities.timecard.HolidayEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

/**
 * 节假日数据访问层
 * 提供节假日数据的查询操作
 */
@Repository
public interface HolidayRepository extends JpaRepository<HolidayEntity, Long> {
    
    /**
     * 查找所有节假日，按日期升序排序
     * @return 所有节假日列表
     */
    List<HolidayEntity> findAllByOrderByHolidayDateAsc();
    
    /**
     * 查找指定年份的节假日
     * @param year 年份
     * @return 该年份的节假日列表
     */
    @Query("SELECT h FROM HolidayEntity h WHERE EXTRACT(YEAR FROM h.holidayDate) = :year " +
           "ORDER BY h.holidayDate ASC")
    List<HolidayEntity> findByYear(@Param("year") int year);
    
    /**
     * 查找指定年月的节假日
     * @param year 年份
     * @param month 月份 (1-12)
     * @return 该年月的节假日列表
     */
    @Query("SELECT h FROM HolidayEntity h WHERE EXTRACT(YEAR FROM h.holidayDate) = :year " +
           "AND EXTRACT(MONTH FROM h.holidayDate) = :month ORDER BY h.holidayDate ASC")
    List<HolidayEntity> findByYearAndMonth(@Param("year") int year, @Param("month") int month);
    
    /**
     * 检查指定日期是否为节假日
     * @param date 日期
     * @return true 如果是节假日，false 否则
     */
    boolean existsByHolidayDate(LocalDate date);
    
    /**
     * 查找所有激活的节假日，按日期升序排序
     * @return 所有激活的节假日列表
     */
    List<HolidayEntity> findAllByIsActiveTrueOrderByHolidayDateAsc();
    
    /**
     * 查找指定年份的激活节假日
     * @param year 年份
     * @return 该年份的激活节假日列表
     */
    @Query("SELECT h FROM HolidayEntity h WHERE EXTRACT(YEAR FROM h.holidayDate) = :year " +
           "AND h.isActive = true ORDER BY h.holidayDate ASC")
    List<HolidayEntity> findByYearAndIsActiveTrue(@Param("year") int year);
    
    /**
     * 查找指定年月的激活节假日
     * @param year 年份
     * @param month 月份 (1-12)
     * @return 该年月的激活节假日列表
     */
    @Query("SELECT h FROM HolidayEntity h WHERE EXTRACT(YEAR FROM h.holidayDate) = :year " +
           "AND EXTRACT(MONTH FROM h.holidayDate) = :month AND h.isActive = true " +
           "ORDER BY h.holidayDate ASC")
    List<HolidayEntity> findByYearAndMonthAndIsActiveTrue(@Param("year") int year, @Param("month") int month);
    
    /**
     * 按国家查找节假日
     * @param country 国家代码
     * @return 该国家的节假日列表
     */
    List<HolidayEntity> findByCountryAndIsActiveTrueOrderByHolidayDateAsc(String country);
    
    /**
     * 检查指定日期和国家是否已存在激活的节假日
     * @param date 日期
     * @param country 国家代码
     * @return true 如果已存在，false 否则
     */
    boolean existsByHolidayDateAndCountryAndIsActiveTrue(LocalDate date, String country);
    
    /**
     * 查找指定日期和国家的激活节假日
     * @param date 日期
     * @param country 国家代码
     * @return 节假日实体（如果存在）
     */
    java.util.Optional<HolidayEntity> findByHolidayDateAndCountryAndIsActiveTrue(LocalDate date, String country);
    
    /**
     * 按年份和国家查找激活节假日
     * @param year 年份
     * @param country 国家代码
     * @return 该年份和国家的激活节假日列表
     */
    @Query("SELECT h FROM HolidayEntity h WHERE EXTRACT(YEAR FROM h.holidayDate) = :year " +
           "AND h.country = :country AND h.isActive = true ORDER BY h.holidayDate ASC")
    List<HolidayEntity> findByYearAndCountry(@Param("year") int year, @Param("country") String country);
    
    /**
     * 检查指定日期和国家是否已存在节假日（不检查isActive）
     * @param date 日期
     * @param country 国家代码
     * @return true 如果已存在，false 否则
     */
    boolean existsByHolidayDateAndCountry(LocalDate date, String country);
    
    /**
     * 查找指定日期和国家的节假日（不检查isActive）
     * @param date 日期
     * @param country 国家代码
     * @return 节假日实体（如果存在）
     */
    java.util.Optional<HolidayEntity> findByHolidayDateAndCountry(LocalDate date, String country);
}

