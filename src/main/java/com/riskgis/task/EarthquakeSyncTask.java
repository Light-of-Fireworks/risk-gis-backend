package com.riskgis.task;

import com.riskgis.service.EarthquakeService;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class EarthquakeSyncTask {

    private final EarthquakeService earthquakeService;

    public EarthquakeSyncTask(EarthquakeService earthquakeService) {
        this.earthquakeService = earthquakeService;
    }

    @Scheduled(fixedRate = 300000)
    public void syncEarthquakeData() {
        earthquakeService.syncEarthquakeData();
    }
}
