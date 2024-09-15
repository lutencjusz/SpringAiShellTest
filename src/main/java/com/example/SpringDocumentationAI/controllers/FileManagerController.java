package com.example.SpringDocumentationAI.controllers;

import com.example.SpringDocumentationAI.services.FileManagerService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;

@Slf4j
@RestController
public class FileManagerController {

    @Value("${SOURCE_PATH}")
    private String SOURCE_PATH;

    @Autowired
    private FileManagerService fileManagerService;

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

        try {
            fileManagerService.saveFile(file);
            log.info("Plik '{}' został zapisany", file.getOriginalFilename());
            return ResponseEntity.ok(true);
        } catch (IOException e) {
            String message = e.getMessage();
            if (e.getMessage().contains(SOURCE_PATH)) {
                log.error("Plik '{}' próbujesz ładować ze ścieżki docelowej '{}'", file.getOriginalFilename(), SOURCE_PATH, e);
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(false);
            } else {
                log.error("Plik '{}' nie został zapisany.", file.getOriginalFilename(), e);
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(false);
            }
        } catch (NullPointerException e) {
            log.error("Plik nie został wprowadzony", e);
        } catch (SecurityException e) {
            log.error("Błąd zapisu pliku", e);
        }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(false);
    }

    /**
     * Pobieranie pliku ze ścieżki w przeglądarce np. <a href="http://localhost:8080/download?fileName=plik.txt">link</a>
     * Plik zostanie pobrany z lokalizacji C:\Install\plik.txt
     *
     * @param fileName - nazwa pliku do pobrania
     * @return ResponseEntity<Resource> - zwraca meta dane pliku do pobrania
     */
    @GetMapping("/download")
    public ResponseEntity<Resource> downloadFile(@RequestParam("fileName") String fileName) {
        log.info("Normalne pobieranie pliku z /download: '{}'", fileName);
        try {
            File fileToDownload = fileManagerService.getDownloadFile(fileName);
            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + fileName + "\"")
                    .contentLength(fileToDownload.length())
                    .contentType(MediaType.APPLICATION_OCTET_STREAM)
                    .body(new InputStreamResource(Files.newInputStream(fileToDownload.toPath())));
        } catch (IOException e) {
            log.error("Plik '{}' nie został pobrany, komunikat błedu: {}", fileName, e.getMessage());
        }
        return ResponseEntity.notFound().build();
    }

    @GetMapping("/download-faster")
    public ResponseEntity<Resource> downloadFileFaster(@RequestParam("fileName") String fileName) {
        log.error("Przyspieszone pobieranie pliku z /download-faster: '{}' z serwera", fileName);
        try {
            File fileToDownload = fileManagerService.getDownloadFile(fileName);
            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + fileName + "\"")
                    .contentLength(fileToDownload.length())
                    .contentType(MediaType.APPLICATION_OCTET_STREAM)
                    .body(new FileSystemResource(fileToDownload));
        } catch (IOException e) {
            log.info("Plik '{}' nie został pobrany, komunikat błedu: {}", fileName, e.getMessage());
        }
        return ResponseEntity.notFound().build();
    }

    @GetMapping("/files-list")
    public ResponseEntity<List<String>> getFileList() throws IOException {
        List<String> files = fileManagerService.getFileNames();
        return ResponseEntity.ok(files);
    }

    @DeleteMapping("/delete-file")
    @ResponseBody
    public ResponseEntity<String> deleteFile(@RequestParam String fileName) {
        try {
            fileManagerService.deleteFile(fileName);
            log.info("Plik '{}' został usunięty", fileName);
            return ResponseEntity.ok("Plik usunięty pomyślnie");
        } catch (IOException ex) {
            log.error("Plik '{}' nie został usunięty, komunikat błędu: {}", fileName, ex.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Błąd usuwania pliku");
        }
    }
}
