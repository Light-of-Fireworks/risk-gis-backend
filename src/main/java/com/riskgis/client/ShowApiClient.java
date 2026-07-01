package com.riskgis.client;

import com.riskgis.dto.typhoon.ShowApiDetailResponse;
import com.riskgis.dto.typhoon.ShowApiListResponse;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.service.annotation.GetExchange;
import org.springframework.web.service.annotation.HttpExchange;
import org.springframework.web.service.annotation.PostExchange;

@HttpExchange(accept = "application/json")
public interface ShowApiClient {

    @GetExchange("/342-3")
    ShowApiListResponse getTyphoonList(@RequestParam String appKey, @RequestParam String year);

    @PostExchange("/342-2")
    ShowApiDetailResponse getTyphoonDetail(@RequestParam String appKey, @RequestBody MultiValueMap<String, String> body);
}
