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
    private final String sourcePath = dotenv.get("SOURCE_PATH");

    public void saveFile(MultipartFile file) throws IOException, NullPointerException, SecurityException {

        final String absoluteUploadPath = new File("").getAbsolutePath() + File.separator + sourcePath;

        if (file.isEmpty() || file.getOriginalFilename() == null || file.getOriginalFilename().isEmpty()) {
            throw new NullPointerException("Nie wybrano pliku");
        }
        File fileToUpload = new File(absoluteUploadPath + File.separator + file.getOriginalFilename());
        if (!Objects.equals(fileToUpload.getParentFile().toString(), absoluteUploadPath)) {
            throw new SecurityException("Błąd zapisu pliku");
        }
        Files.copy(file.getInputStream(), fileToUpload.toPath(), StandardCopyOption.REPLACE_EXISTING);
    }

    public File getDownloadFile(String fileName) throws IOException {

        final String absoluteUploadPath = new File("").getAbsolutePath() + File.separator + sourcePath;

        if (fileName == null || fileName.isEmpty()) {
            throw new NullPointerException("Nie wybrano pliku");
        }
        File fileToDownload = new File(absoluteUploadPath + File.separator + fileName);
        if (!fileToDownload.exists()) {
            throw new IOException("Plik '" + fileName + "' nie istnieje");
        }
        if (!Objects.equals(fileToDownload.getParentFile().toString(), absoluteUploadPath)) {
            throw new SecurityException("Błąd odczytu pliku");
        }
        return fileToDownload;
    }

    public List<String> getFileNames() throws IOException {
        final String absoluteUploadPath = new File("").getAbsolutePath() + File.separator + sourcePath;
        File file = new File(absoluteUploadPath);
        if (!file.exists()) {
            throw new IOException("Katalog '" + absoluteUploadPath + "' nie istnieje");
        }
        return List.of(Objects.requireNonNull(file.list()));
    }
}
