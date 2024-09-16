package com.example.SpringDocumentationAI.controllers;

import com.example.SpringDocumentationAI.ReferenceDocsLoader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
public class ProgressController {

    @Autowired
    ReferenceDocsLoader referenceDocsLoader;

    @GetMapping("/progress")
    public Map<String, Integer> getProgress() {
        return referenceDocsLoader.getPercentProgress();
    }

    @GetMapping("/start-progress")
    public ResponseEntity<Boolean> startProgress() {
        referenceDocsLoader.resetVectorStore();
        referenceDocsLoader.init();
        return ResponseEntity.ok(true);
    }
}
