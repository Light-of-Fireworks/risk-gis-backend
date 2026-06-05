package com.riskgis.task;

import com.riskgis.service.TyphoonService;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class TyphoonSyncTask {

    private final TyphoonService typhoonService;

    public TyphoonSyncTask(TyphoonService typhoonService) {
        this.typhoonService = typhoonService;
    }

    @Scheduled(fixedRate = 7200000)
    public void syncTyphoonData() {
        typhoonService.syncTyphoonData();
    }
}
