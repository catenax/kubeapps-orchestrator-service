package com.poc.kubeappswrapper.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.swagger.v3.oas.annotations.servers.Server;
import org.springframework.context.annotation.Configuration;

@OpenAPIDefinition(
        info = @Info(
                title = "Kubeapps Orchesteator API",
                description = "" +
                        "This Service handles all Orchestrator and Kubeapps related operations" +
                        " - CRUD for kubeapps packages using Orchestrator service, " +
                        " Displaying the triggers and their details and" +
                        " kubeapps CRUD apis",
                version = "1.0"
        ),
        servers = @Server(url = "http://dih-cloud.net/api/v1")
)
@SecurityScheme(
        name = "bearerAuth",
        type = SecuritySchemeType.HTTP,
        bearerFormat = "JWT",
        scheme = "bearer"
)
@Configuration
public class OpenApiConfig {


}
