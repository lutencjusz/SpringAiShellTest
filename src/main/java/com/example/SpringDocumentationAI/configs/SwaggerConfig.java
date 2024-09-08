package com.example.SpringDocumentationAI.configs;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.List;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI defineOpenApi() throws UnknownHostException {
        InetAddress inetAddress = InetAddress.getLocalHost();
        Server server = new Server();
        server.setUrl("http://" + inetAddress + ":8080");
        server.setDescription("Asystent Chat GPT");
        server.setDescription("Asystent umożliwiający zadawanie pytań i uzyskiwanie odpowiedzi za pomocą GPT-3 na podstawie zasobów dokumentacji w formie plików PDF lub epub");
        Contact myContact = new Contact();
        myContact.setName("Michał Sobieraj");
        myContact.setEmail("michal.sobieraj@sopim.pl");
        Info information = new Info()
                .title("Asystent Chat GPT")
                .version("1.0")
                .description("To są operacje REST API służące do komunikacji z asystentem Chat GPT")
                .license(new License().name("Apache 2.0").url("http://springdoc.org"))
                .termsOfService("http://swagger.io/terms/")
                .summary("API do zadawania pytań i uzyskiwania odpowiedzi za pomocą Chat GPT")
                .contact(myContact);
        return new OpenAPI().info(information).servers(List.of(server));
    }
}
