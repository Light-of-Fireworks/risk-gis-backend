package com.riskgis.config;

import com.riskgis.client.AmapClient;
import com.riskgis.client.ApihzClient;
import com.riskgis.client.FloodWarningClient;
import com.riskgis.client.OpenTopoDataClient;
import com.riskgis.client.ShowApiClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.support.RestClientAdapter;
import org.springframework.web.service.invoker.HttpServiceProxyFactory;

@Configuration
public class RestClientConfig {

    @Bean
    public ShowApiClient showApiClient(@Value("${showapi.base-url}") String baseUrl) {
        RestClient client = RestClient.builder().baseUrl(baseUrl).build();
        return HttpServiceProxyFactory
                .builderFor(RestClientAdapter.create(client))
                .build()
                .createClient(ShowApiClient.class);
    }

    @Bean
    public AmapClient amapClient(@Value("${amap.base-url}") String baseUrl) {
        RestClient client = RestClient.builder().baseUrl(baseUrl).build();
        return HttpServiceProxyFactory
                .builderFor(RestClientAdapter.create(client))
                .build()
                .createClient(AmapClient.class);
    }

    @Bean
    public ApihzClient apihzClient(@Value("${apihz.base-url}") String baseUrl) {
        RestClient client = RestClient.builder().baseUrl(baseUrl).build();
        return HttpServiceProxyFactory
                .builderFor(RestClientAdapter.create(client))
                .build()
                .createClient(ApihzClient.class);
    }

    @Bean
    public FloodWarningClient floodWarningClient(@Value("${flood-warning.base-url}") String baseUrl) {
        RestClient client = RestClient.builder().baseUrl(baseUrl).build();
        return HttpServiceProxyFactory
                .builderFor(RestClientAdapter.create(client))
                .build()
                .createClient(FloodWarningClient.class);
    }

    @Bean
    public OpenTopoDataClient openTopoDataClient(@Value("${opentopodata.base-url}") String baseUrl) {
        RestClient client = RestClient.builder().baseUrl(baseUrl).build();
        return HttpServiceProxyFactory
                .builderFor(RestClientAdapter.create(client))
                .build()
                .createClient(OpenTopoDataClient.class);
    }
}
