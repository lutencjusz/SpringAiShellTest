package com.example.SpringDocumentationAI;

import com.example.SpringDocumentationAI.readers.EpubDocumentContent;
import com.example.SpringDocumentationAI.readers.PdfDocumentContent;
import com.example.SpringDocumentationAI.readers.ReaderDocumentInterface;
import jakarta.annotation.PostConstruct;
import nl.siegmann.epublib.domain.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.transformer.splitter.TokenTextSplitter;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Component
public class ReferenceDocsLoader {

    private static final String RESOURCE_AND_EXTENSION_CLASSPATH = "classpath:/docs/*.*";

    private static final Logger logger = LoggerFactory.getLogger(ReferenceDocsLoader.class);
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
    }

    public static String readResourceAsString(Resource resource) {
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(resource.getInputStream(), resource.getInputEncoding()));
            return reader.lines().collect(Collectors.joining("\n"));
        } catch (Exception e) {
            logger.info("Błąd przy pobieraniu zasobów jako String: ", e);
            throw new RuntimeException(e);
        }
    }

    private int getCountVectorStore() {
        return jdbcClient.sql("SELECT COUNT(*) FROM vector_store")
                .query(Integer.class)
                .single();
    }

    @PostConstruct
    public void init() {
        int count = getCountVectorStore();
        logger.info("Loaded {} chunks from DB", count);
        if (count == 0) {
            logger.info("Loading documents from resources...");
            var textSplitter = new TokenTextSplitter();
            resources.forEach(resource -> {
                try {
                    for (ReaderDocumentInterface readerDocument : readersDocument) {
                        if (Objects.requireNonNull(resource.getFilename()).endsWith(readerDocument.getDocumentType())) {
                            vectorStore.accept(textSplitter.apply(readerDocument.getDocumentContent(resource)));
                        }
                    }
                } catch (IOException e) {
                    logger.error("Error: ", e);
                    throw new RuntimeException(e);
                }
                logger.info("Splitted up from resource '{}'. For now, totally splitt up {} chunks", resource.getFilename(), getCountVectorStore());
            });
            logger.info("Splitting up chanks has finished, splited up {} chunks", getCountVectorStore());
        } else {
            logger.info("Chanks already splited up, skipping load resources.");
        }
    }
}
