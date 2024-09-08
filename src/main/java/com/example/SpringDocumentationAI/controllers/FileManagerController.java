package com.example.SpringDocumentationAI.controllers;

import com.example.SpringDocumentationAI.services.FileManagerService;
import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

@RestController
public class FileManagerController {

    Dotenv dotenv = Dotenv.load();

    private final FileManagerService fileManagerService;
    private static final Logger log = Logger.getLogger(FileManagerController.class.getName());

    public FileManagerController(FileManagerService fileManagerService) {
        this.fileManagerService = fileManagerService;
    }

    /**
     * Wysyłanie pliku na serwer do katalogu C:\Install
     * Najlepiej użyć programu Postman do wysłania pliku na serwer z opcją POST -> Body -> form-data -> typ klucza: file
     * *
     *
     * @param file - plik do zapisania
     * @return - zwraca true jeśli plik został zapisany w katalogu serwera, w przeciwnym wypadku false
     */
    @PostMapping("/upload")
    public ResponseEntity<Boolean> uploadFile(@RequestParam("file") MultipartFile file) {
        String SOURCE_PATH = dotenv.get("SOURCE_PATH");
        try {
            fileManagerService.saveFile(file);
            log.log(Level.INFO, "Plik '" + file.getOriginalFilename() + "' został zapisany");
            return ResponseEntity.ok(true);
        } catch (IOException e) {
            String message = e.getMessage();
            if (SOURCE_PATH != null && e.getMessage().contains(SOURCE_PATH)) {
                log.log(Level.SEVERE, "Plik '" + file.getOriginalFilename() + "' próbujesz ładować ze ścieżki docelowej '" + SOURCE_PATH + "'", e);
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(false);
            } else {
                log.log(Level.SEVERE, "Plik '" + file.getOriginalFilename() + "' nie został zapisany.", e);
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(false);
            }
        } catch (NullPointerException e) {
            log.log(Level.SEVERE, "Plik nie został wprowadzony", e);
        } catch (SecurityException e) {
            log.log(Level.SEVERE, "Błąd zapisu pliku", e);
        }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(false);
    }

    /**
     * Pobieranie pliku ze ścieżki w przeglądarce np. http://localhost:8080/download?fileName=plik.txt
     * Plik zostanie pobrany z lokalizacji C:\Install\plik.txt
     *
     * @param fileName - nazwa pliku do pobrania
     * @return ResponseEntity<Resource> - zwraca meta dane pliku do pobrania
     */
    @GetMapping("/download")
    public ResponseEntity<Resource> downloadFile(@RequestParam("fileName") String fileName) {
        log.log(Level.INFO, "Normalne pobieranie pliku z /download: '" + fileName + "'");
        try {
            File fileToDownload = fileManagerService.getDownloadFile(fileName);
            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + fileName + "\"")
                    .contentLength(fileToDownload.length())
                    .contentType(MediaType.APPLICATION_OCTET_STREAM)
                    .body(new InputStreamResource(Files.newInputStream(fileToDownload.toPath())));
        } catch (IOException e) {
            log.log(Level.SEVERE, "Plik '" + fileName + "' nie został pobrany", e.getMessage());
        }
        return ResponseEntity.notFound().build();
    }

    @GetMapping("/download-faster")
    public ResponseEntity<Resource> downloadFileFaster(@RequestParam("fileName") String fileName) {
        log.log(Level.INFO, "Przyspieszone pobieranie pliku z /download-faster: '" + fileName + "' z serwera");
        try {
            File fileToDownload = fileManagerService.getDownloadFile(fileName);
            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + fileName + "\"")
                    .contentLength(fileToDownload.length())
                    .contentType(MediaType.APPLICATION_OCTET_STREAM)
                    .body(new FileSystemResource(fileToDownload));
        } catch (IOException e) {
            log.log(Level.SEVERE, "Plik '" + fileName + "' nie został pobrany", e.getMessage());
        }
        return ResponseEntity.notFound().build();
    }

    @GetMapping("/files-list")
    public ResponseEntity<List<String>> getFileList() throws IOException {
        List<String> files = fileManagerService.getFileNames();
        return ResponseEntity.ok(files);
    }
}
