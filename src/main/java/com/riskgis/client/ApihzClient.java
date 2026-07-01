package com.riskgis.client;

import com.riskgis.dto.earthquake.EarthquakeResponse;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.service.annotation.GetExchange;
import org.springframework.web.service.annotation.HttpExchange;

@HttpExchange(accept = "application/json")
public interface ApihzClient {

    @GetExchange("/api/tianqi/dizhen.php")
    EarthquakeResponse getEarthquake(@RequestParam String id, @RequestParam String key);
}
