package com.riskgis.client;

import com.riskgis.dto.opentopodata.OpenTopoDataResponse;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.service.annotation.GetExchange;
import org.springframework.web.service.annotation.HttpExchange;

@HttpExchange(accept = "application/json")
public interface OpenTopoDataClient {

    @GetExchange("/v1/srtm90m")
    OpenTopoDataResponse getElevation(@RequestParam String locations);
}
