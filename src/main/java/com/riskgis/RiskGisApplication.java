package com.riskgis;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@MapperScan("com.riskgis.mapper")
@EnableScheduling
public class RiskGisApplication {
    public static void main(String[] args) {
        SpringApplication.run(RiskGisApplication.class, args);
    }
}
