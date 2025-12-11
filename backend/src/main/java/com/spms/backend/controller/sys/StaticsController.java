package com.spms.backend.controller.sys;

import com.spms.backend.controller.dto.sys.AvailableStatisticsDataPointDto;
import com.spms.backend.controller.dto.sys.StatisticResponseDTO;
import com.spms.backend.model.SystemStatisticsModel;  // Added import
import com.spms.backend.service.sys.SystemStatisticsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/statistics")
public class StaticsController {

    @Autowired
    private SystemStatisticsService statisticsService;

    @GetMapping("/available-data-points")
    public ResponseEntity<List<AvailableStatisticsDataPointDto>> getAvailableDataPoints() {
        return ResponseEntity.ok(statisticsService.getAvailableStatisticsDataPoints().stream().map(x -> {
            AvailableStatisticsDataPointDto dto = new AvailableStatisticsDataPointDto();
            dto.setName(x.getName());
            dto.setDescription(x.getDescription());
            return dto;
        }).toList());
    }


    @GetMapping("/latest/{name}")
    public ResponseEntity<StatisticResponseDTO> getLatestStatistic(
        @PathVariable String name) {
        
        SystemStatisticsModel model = statisticsService.getLatestStatisticByName(name);
        StatisticResponseDTO response = new StatisticResponseDTO();
        if (model == null) {
            response.setAsOfDate(new Date());
            response.setValue(null);
            response.setDescription("");
            response.setName(name);
        } else {
            response.setName(model.getName());
            response.setDescription(model.getDescription());
            response.setAsOfDate(model.getAsOfDate());
            response.setValue(model.getValue());
        }
        return ResponseEntity.ok(response);
    }
    
    @GetMapping
    public ResponseEntity<List<StatisticResponseDTO>> getStatisticsByDateRange(
        @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) Date start,
        @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) Date end) {
        
        List<SystemStatisticsModel> models = statisticsService.getStatisticsByDateRange(start, end);
        List<StatisticResponseDTO> response = models.stream()
            .map(model -> {
                StatisticResponseDTO dto = new StatisticResponseDTO();
                dto.setName(model.getName());
                dto.setDescription(model.getDescription());
                dto.setAsOfDate(model.getAsOfDate());
                dto.setValue(model.getValue());
                return dto;
            })
            .collect(Collectors.toList());
        
        return ResponseEntity.ok(response);
    }
}
