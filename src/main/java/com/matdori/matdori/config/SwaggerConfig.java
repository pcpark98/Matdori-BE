package com.matdori.matdori.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springdoc.core.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    @Bean
    public GroupedOpenApi publicApi() {
        return GroupedOpenApi.builder()
                .group("Matdori")
                .pathsToMatch("/**")
                .build();
    }

    @Bean
    public OpenAPI matdoriOpenApi() {
        return new OpenAPI()
                .components(new Components())
                .info(new Info().title("Matdori API")
                        .description("맛도리 프로젝트 API 명세서입니다.")
                        .version("v0.0.1"));
    }
}
