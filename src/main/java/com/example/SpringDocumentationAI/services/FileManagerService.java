package com.example.SpringDocumentationAI.services;

import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.Objects;

@Service
public class FileManagerService {

    Dotenv dotenv = Dotenv.load();

    public void saveFile(MultipartFile file) throws IOException, NullPointerException, SecurityException {

        String FILE_PATH = dotenv.get("SOURCE_PATH");
        if (file.isEmpty() || file.getOriginalFilename() == null || file.getOriginalFilename().isEmpty()) {
            throw new NullPointerException("Nie wybrano pliku");
        }
        File fileToUpload = new File(FILE_PATH + File.separator + file.getOriginalFilename());
        if (!Objects.equals(fileToUpload.getParentFile().toString(), FILE_PATH)) {
            throw new SecurityException("Błąd zapisu pliku");
        }
        Files.copy(file.getInputStream(), fileToUpload.toPath(), StandardCopyOption.REPLACE_EXISTING);
    }

    public File getDownloadFile(String fileName) throws IOException {
        String FILE_PATH = dotenv.get("SOURCE_PATH");
        if (fileName == null || fileName.isEmpty()) {
            throw new NullPointerException("Nie wybrano pliku");
        }
        File fileToDownload = new File(FILE_PATH + File.separator + fileName);
        if (!fileToDownload.exists()) {
            throw new IOException("Plik '" + fileName + "' nie istnieje");
        }
        if (!Objects.equals(fileToDownload.getParentFile().toString(), FILE_PATH)) {
            throw new SecurityException("Błąd odczytu pliku");
        }
        return fileToDownload;
    }

    public List<String> getFileNames() throws IOException {
        String FILE_PATH = dotenv.get("SOURCE_PATH");
        assert FILE_PATH != null;
        File file = new File(FILE_PATH);
        if (!file.exists()) {
            throw new IOException("Katalog '" + FILE_PATH + "' nie istnieje");
        }
        return List.of(Objects.requireNonNull(file.list()));
    }
}
