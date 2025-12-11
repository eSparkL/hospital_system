package com.shanzhu.hospital.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.Contact;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("医院管理系统 API文档")
                        .description("医院管理系统的 API documentation")
                        .version("1.0.0")
                        .contact(new Contact()
                                .name("开发者")
                                .url("https://github.com")
                                .email("dev@example.com")));
    }
}