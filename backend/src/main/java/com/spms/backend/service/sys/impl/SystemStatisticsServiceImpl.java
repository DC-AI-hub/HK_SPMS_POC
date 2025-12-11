package com.spms.backend.service.sys.impl;

import com.spms.backend.controller.dto.sys.SystemStatisticsDTO;
import com.spms.backend.jobs.data.DataPointCollector;
import com.spms.backend.model.AvailableStatisticsDataPointModel;
import com.spms.backend.model.SystemStatisticsModel;
import com.spms.backend.repository.entities.sys.SystemStatistics;
import com.spms.backend.repository.sys.SystemStatisticsRepository;
import com.spms.backend.service.sys.SystemStatisticsService;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;


@Service
public class SystemStatisticsServiceImpl implements SystemStatisticsService {

    private final SystemStatisticsRepository repository;
    private final List<DataPointCollector> dataPointCollectors;


    /**
     * Constructs a new SystemStatisticsServiceImpl with the repository dependency
     * 
     * @param repository System statistics repository
     */
    public SystemStatisticsServiceImpl(SystemStatisticsRepository repository,
                                       List<DataPointCollector> dataPointCollectors) {
        this.repository = repository;
        this.dataPointCollectors = dataPointCollectors;
    }



    @Override
    public void recordStatistic(String name, String description, Date asOfDate, Long value) {
        SystemStatistics entity = new SystemStatistics();
        entity.setName(name);
        entity.setDescription(description);
        entity.setAsOfDate(asOfDate);
        entity.setValue(value);
        SystemStatistics saved = repository.save(entity);
        convertEntityToModel(saved);
    }

    /**
     * Converts DTO to entity object
     * 
     * @param dto Data transfer object
     * @return Converted entity object
     */
    private SystemStatistics convertDTOToEntity(SystemStatisticsDTO dto) {
        SystemStatistics entity = new SystemStatistics();
        entity.setName(dto.getName());
        entity.setDescription(dto.getDescription());
        entity.setAsOfDate(dto.getAsOfDate());
        entity.setValue(dto.getValue());
        return entity;
    }

    /**
     * Retrieves the latest statistic by name ordered by date descending
     * 
     * @param name Name of the statistic to retrieve
     * @return Latest statistic model or null if not found
     */
    @Override
    public SystemStatisticsModel getLatestStatisticByName(String name) {
        List<SystemStatistics> stats = repository.findByNameOrderByAsOfDateDesc(name);
        if (stats.isEmpty()) {
            return null;
        }
        return convertEntityToModel(stats.get(0));
    }


    /**
     * Retrieves statistics within a date range and converts them to models
     * 
     * @param start Start date (inclusive)
     * @param end End date (inclusive)
     * @return List of statistic models in the date range
     */
    @Override
    public List<SystemStatisticsModel> getStatisticsByDateRange(Date start, Date end) {
        return repository.findByAsOfDateBetween(start, end)
                .stream()
                .map(this::convertEntityToModel)
                .collect(Collectors.toList());
    }

    @Override
    public List<AvailableStatisticsDataPointModel> getAvailableStatisticsDataPoints() {
        return dataPointCollectors.stream().map(x->{
            AvailableStatisticsDataPointModel model = new AvailableStatisticsDataPointModel();
            model.setDescription(x.description());
            model.setName(x.name());
            return model;
        }).toList();
    }

    /**
     * Converts entity to model object
     * 
     * @param entity Entity object
     * @return Converted model object
     */
    private SystemStatisticsModel convertEntityToModel(SystemStatistics entity) {
        SystemStatisticsModel model = new SystemStatisticsModel();
        model.setName(entity.getName());
        model.setDescription(entity.getDescription());
        model.setAsOfDate(entity.getAsOfDate());
        model.setValue(entity.getValue());
        return model;
    }
}
