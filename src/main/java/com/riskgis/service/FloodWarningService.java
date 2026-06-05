package com.riskgis.service;

import com.riskgis.model.FloodWarning;

import java.time.LocalDateTime;
import java.util.List;

public interface FloodWarningService {

    void syncFloodWarningData();

    List<FloodWarning> getFloodWarningsByTimeRange(String range);

    List<FloodWarning> getFloodWarningsByDateRange(LocalDateTime startTime, LocalDateTime endTime);
}
