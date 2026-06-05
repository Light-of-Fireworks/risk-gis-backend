package com.riskgis.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * SpringDoc OpenAPI 配置
 * <p>
 * 使用 SpringDoc 自动生成 OpenAPI 文档，无需 Swagger 注解。
 * API 文档通过 Javadoc 注释自动生成。
 * </p>
 */
@Configuration
public class SwaggerConfig {

    /**
     * 配置 OpenAPI 信息
     * <p>
     * 设置 API 文档的基本信息，包括标题、版本、描述和联系方式。
     * 同时配置 JWT 认证方案。
     * </p>
     *
     * @return OpenAPI 配置对象
     */
    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("风险地理信息系统 API")
                        .version("1.0.0")
                        .description("风险地理信息系统后端 API 文档")
                        .contact(new Contact()
                                .name("RiskGIS Team")
                                .email("admin@riskgis.com")))
                .addSecurityItem(new SecurityRequirement().addList("Bearer"))
                .components(new Components()
                        .addSecuritySchemes("Bearer",
                                new SecurityScheme()
                                        .type(SecurityScheme.Type.HTTP)
                                        .scheme("bearer")
                                        .bearerFormat("JWT")));
    }
}
