package com.example.SpringDocumentationAI.controllers;

import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Controller
public class FileManagerGuiController {

    Dotenv dotenv = Dotenv.load();

    private void addFiles(Model model) throws IOException {
        String SOURCE_PATH = dotenv.get("SOURCE_PATH");
        assert SOURCE_PATH != null;
        try (DirectoryStream<Path> files = Files.newDirectoryStream(Path.of(SOURCE_PATH))) {
            model.addAttribute("files", StreamSupport.stream(files.spliterator(),
                            false)
                    .map(Path::getFileName)
                    .map(Path::toString)
                    .collect(Collectors.toList()));
        }
    }

    @GetMapping("/load-data")
    public String uploader(Model model) throws IOException {
        addFiles(model);
        return "uploader";
    }

    @GetMapping("/files")
    public String listFiles(Model model) throws IOException {
        addFiles(model);
        return "file-list";
    }
}
