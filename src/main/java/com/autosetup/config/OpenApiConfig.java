package com.autosetup.config;

import org.springdoc.core.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.swagger.v3.oas.annotations.servers.Server;

@OpenAPIDefinition(
        info = @Info(
                title = "Auto Setup API information",
                description = "" +
                        "This Service handles all auto setup related operations",
                version = "1.0"
        ),
        servers = @Server(url = "https://orchestrator.cx.dih-cloud.com")
)
@SecurityScheme(
        name = "bearerAuth",
        type = SecuritySchemeType.HTTP,
        bearerFormat = "JWT",
        scheme = "bearer"
)
@Configuration
public class OpenApiConfig {

	@Bean
	public GroupedOpenApi externalOpenApi() {
		String[] paths = {"/internal/**"};
		return GroupedOpenApi.builder().group("autosetup").pathsToExclude(paths)
				.build();
	}
	
	@Bean
	public GroupedOpenApi internalOpenApi() {
		String[] paths = {"/internal/**"};
		return GroupedOpenApi.builder().group("internal").pathsToMatch(paths)
				.build();
	}

}
