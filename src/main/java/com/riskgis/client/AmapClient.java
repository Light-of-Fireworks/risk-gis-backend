package com.riskgis.client;

import com.riskgis.dto.amap.AmapDistrictResponse;
import com.riskgis.dto.amap.AmapInputTipsResponse;
import com.riskgis.dto.amap.AmapRegeoResponse;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.service.annotation.GetExchange;
import org.springframework.web.service.annotation.HttpExchange;

@HttpExchange(accept = "application/json")
public interface AmapClient {

    @GetExchange("/v3/assistant/inputtips")
    AmapInputTipsResponse inputTips(
            @RequestParam String key,
            @RequestParam String keywords,
            @RequestParam(defaultValue = "") String city,
            @RequestParam(defaultValue = "all") String datatype);

    @GetExchange("/v3/geocode/regeo")
    AmapRegeoResponse reverseGeocode(
            @RequestParam String key,
            @RequestParam String location);

    @GetExchange("/v3/config/district")
    AmapDistrictResponse district(
            @RequestParam String key,
            @RequestParam String keywords,
            @RequestParam int subdistrict,
            @RequestParam String extensions);
}
