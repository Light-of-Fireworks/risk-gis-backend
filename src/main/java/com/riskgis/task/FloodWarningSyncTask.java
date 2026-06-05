package com.riskgis.task;

import com.riskgis.service.FloodWarningService;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class FloodWarningSyncTask {

    private final FloodWarningService floodWarningService;

    public FloodWarningSyncTask(FloodWarningService floodWarningService) {
        this.floodWarningService = floodWarningService;
    }

    @Scheduled(fixedRate = 300000)
    public void syncFloodWarningData() {
        floodWarningService.syncFloodWarningData();
    }
}
