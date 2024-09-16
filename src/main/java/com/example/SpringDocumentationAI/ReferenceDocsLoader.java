package com.example.SpringDocumentationAI;

import com.example.SpringDocumentationAI.readers.EpubDocumentContent;
import com.example.SpringDocumentationAI.readers.PdfDocumentContent;
import com.example.SpringDocumentationAI.readers.ReaderDocumentInterface;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import nl.siegmann.epublib.domain.Resource;
import org.springframework.ai.transformer.splitter.TokenTextSplitter;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Component
public class ReferenceDocsLoader {

    private static final String RESOURCE_AND_EXTENSION_CLASSPATH = "classpath:/docs/*.*";
    private int progress = 0;
    private final int maxResourcesForProgress;

    private final JdbcClient jdbcClient;
    private final VectorStore vectorStore;
    ReaderDocumentInterface[] readersDocument = new ReaderDocumentInterface[]{
            new EpubDocumentContent(),
            new PdfDocumentContent()
    };

    List<org.springframework.core.io.Resource> resources;

    public ReferenceDocsLoader(JdbcClient jdbcClient, VectorStore vectorStore) throws IOException {
        this.jdbcClient = jdbcClient;
        this.vectorStore = vectorStore;
        PathMatchingResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
        org.springframework.core.io.Resource[] resourcesTable = resolver.getResources(RESOURCE_AND_EXTENSION_CLASSPATH);
        this.resources = Arrays.asList(resourcesTable);
        this.maxResourcesForProgress = resources.size() + 1;
    }

    public static String readResourceAsString(Resource resource) {
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(resource.getInputStream(), resource.getInputEncoding()));
            return reader.lines().collect(Collectors.joining("\n"));
        } catch (Exception e) {
            log.info("Błąd przy pobieraniu zasobów jako String: ", e);
            throw new RuntimeException(e);
        }
    }

    private int getCountVectorStore() {
        return jdbcClient.sql("SELECT COUNT(*) FROM vector_store")
                .query(Integer.class)
                .single();
    }

    public void resetVectorStore() {
        jdbcClient.sql("DELETE FROM vector_store")
                .update();
    }

    @PostConstruct
    public void init() {
        int count = getCountVectorStore();
        log.info("Loaded {} chunks from DB", count);
        if (count == 0) {
            log.info("Loading documents from resources...");
            this.progress = 0;
            var textSplitter = new TokenTextSplitter();
            resources.forEach(resource -> {
                this.progress += 1;
                try {
                    for (ReaderDocumentInterface readerDocument : readersDocument) {
                        if (Objects.requireNonNull(resource.getFilename()).endsWith(readerDocument.getDocumentType())) {
                            vectorStore.accept(textSplitter.apply(readerDocument.getDocumentContent(resource)));
                        }
                    }
                } catch (IOException e) {
                    log.error("Error: ", e);
                    throw new RuntimeException(e);
                }
                log.info("Splitted up from resource '{}'. For now, totally splitt up {} chunks", resource.getFilename(), getCountVectorStore());
            });
            log.info("Splitting up chanks has finished, splited up {} chunks", getCountVectorStore());
        } else {
            log.info("Chanks already splited up, skipping load resources.");
        }
        this.progress = maxResourcesForProgress;
    }

    public Map<String, Integer> getPercentProgress() {
        Map<String, Integer> response = new HashMap<>();
        double percent = ((double) this.progress / maxResourcesForProgress) * 100;
        System.out.println("Pobieranie postępu..." + percent);
        response.put("percentage", (int) percent);
        return response;
    }
}
