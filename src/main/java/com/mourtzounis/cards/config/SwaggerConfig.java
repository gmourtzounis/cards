package com.mourtzounis.cards.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import org.springframework.context.annotation.Configuration;

@OpenAPIDefinition(info = @Info(title = "Cards App", version = "v1", description = "Create cards and manage your tasks"))
@Configuration
public class SwaggerConfig {
}
