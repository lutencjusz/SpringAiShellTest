package com.example.SpringDocumentationAI;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.shell.command.annotation.CommandScan;

@CommandScan
@SpringBootApplication
public class SpringDocumentationAiApplication {

	public static void main(String[] args) {
		SpringApplication.run(SpringDocumentationAiApplication.class, args);
	}

}
