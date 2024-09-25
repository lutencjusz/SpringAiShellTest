package com.example.SpringDocumentationAI;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.stream.Collectors;

@Service
public class PropertiesFileLoader {

    @Autowired
    private ResourceLoader resourceLoader;

    public String loadPropertiesFileToString() throws IOException {
        // Zbudowanie ścieżki do pliku messages_<localeCode>.properties
        String filePath = "classpath:messages_pl.properties";

        // Wczytanie pliku jako zasobu
        Resource resource = resourceLoader.getResource(filePath);

        // Przetworzenie zawartości pliku na String
        String content = Files.lines(Paths.get(resource.getURI()))
                .collect(Collectors.joining(System.lineSeparator()));

        return content;
    }
}

