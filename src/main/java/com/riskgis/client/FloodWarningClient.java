package com.riskgis.client;

import com.riskgis.dto.flood.FloodWarningResponse;
import org.springframework.web.service.annotation.GetExchange;
import org.springframework.web.service.annotation.HttpExchange;

@HttpExchange(accept = "application/json")
public interface FloodWarningClient {

    @GetExchange("/warn-release-api/api/warninfo.custom/0Tmha3BncGVQbzZDb0tlcVp1MkorOThaYmhoMUdWL2taTlppa3hPdDY2az0=")
    FloodWarningResponse getWarnings();
}
