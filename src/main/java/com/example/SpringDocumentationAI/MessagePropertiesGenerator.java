package com.example.SpringDocumentationAI;

import com.example.SpringDocumentationAI.services.SpringAssistantService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Controller;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

@Slf4j
@Controller
public class MessagePropertiesGenerator {

    @Autowired
    PropertiesFileLoader propertiesFileLoader;

    MessagePropertiesGenerator(PropertiesFileLoader propertiesFileLoader) {
        this.propertiesFileLoader = propertiesFileLoader;
    }

    @Autowired
    private SpringAssistantService springAssistantService;

    @Autowired
    private ResourceLoader resourceLoader;

    public void createPropertiesFile(String localeCode) throws IOException {
        String filePath = "src/main/resources/messages_" + localeCode.substring(0, 2) + ".properties";
        File file = new File(filePath);
        if (file.exists()) {
            log.info("Plik 'messages_" + localeCode + ".properties' z tą lokalizacją już istnieje");
            return;
        }
        String content = propertiesFileLoader.loadPropertiesFileToString();
        String translatedContent = springAssistantService.translate(content, localeCode);
        try (FileOutputStream fileOutputStream = new FileOutputStream(file)) {
            fileOutputStream.write(translatedContent.getBytes());
            fileOutputStream.close();
            log.info("Plik 'messages_" + localeCode + ".properties' został zapisany pomyślnie.");
        }
    }
}
