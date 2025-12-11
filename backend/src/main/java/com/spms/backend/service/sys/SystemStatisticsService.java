package com.spms.backend.service.sys;

import com.spms.backend.model.AvailableStatisticsDataPointModel;
import com.spms.backend.model.SystemStatisticsModel;

import java.util.Date;
import java.util.List;

public interface SystemStatisticsService {


    void recordStatistic(String name, String description, Date asOfDate, Long value);

    /**
     * Retrieves the latest statistic entry by name
     * 
     * @param name Name of the statistic to retrieve
     * @return Latest statistic model or null if not found
     */
    SystemStatisticsModel getLatestStatisticByName(String name);

    /**
     * Retrieves statistics within a specified date range
     * 
     * @param start Start date (inclusive)
     * @param end End date (inclusive)
     * @return List of statistic models in the date range
     */
    List<SystemStatisticsModel> getStatisticsByDateRange(Date start, Date end);

    List<AvailableStatisticsDataPointModel> getAvailableStatisticsDataPoints();
}
