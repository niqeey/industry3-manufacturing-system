package com.industry3.manufacturing.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class OpenAPIConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        Server localServer = new Server();
        localServer.setUrl("http://localhost:8080");
        localServer.setDescription("Development Server");

        Contact contact = new Contact();
        contact.setName("Manufacturing Team");
        contact.setEmail("admin@manufacturing.com");

        License license = new License();
        license.setName("MIT License");
        license.setUrl("https://opensource.org/licenses/MIT");

        Info info = new Info()
                .title("Industry 3.0 Manufacturing Management API")
                .version("1.0.0")
                .description("Comprehensive API for managing manufacturing operations, including products, work orders, inventory, and quality control.")
                .contact(contact)
                .license(license);

        return new OpenAPI()
                .info(info)
                .servers(List.of(localServer));
    }
}
