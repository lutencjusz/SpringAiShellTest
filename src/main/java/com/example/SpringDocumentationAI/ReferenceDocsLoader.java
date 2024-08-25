package com.example.SpringDocumentationAI;

import jakarta.annotation.PostConstruct;
import nl.siegmann.epublib.domain.Book;
import nl.siegmann.epublib.epub.EpubReader;
import nl.siegmann.epublib.domain.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.document.Document;
import org.springframework.ai.reader.ExtractedTextFormatter;
import org.springframework.ai.reader.pdf.PagePdfDocumentReader;
import org.springframework.ai.reader.pdf.config.PdfDocumentReaderConfig;
import org.springframework.ai.transformer.splitter.TokenTextSplitter;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Component;

import java.io.*;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class ReferenceDocsLoader {

    private static final Logger logger = LoggerFactory.getLogger(ReferenceDocsLoader.class);
    private final JdbcClient jdbcClient;
    private final VectorStore vectorStore;

    @Value("classpath:/docs/spring-boot-reference.pdf")
    private org.springframework.core.io.Resource pdfResource;

    @Value("classpath:/docs/Sapiens._Od_zwierzat_do_bogow.epub")
    private org.springframework.core.io.Resource epubResource;

    public ReferenceDocsLoader(JdbcClient jdbcClient, VectorStore vectorStore) {
        this.jdbcClient = jdbcClient;
        this.vectorStore = vectorStore;
    }

    private List<Document> getPdfDocumentContent(org.springframework.core.io.Resource pdfResource) {
        var config = PdfDocumentReaderConfig.builder()
                .withPageExtractedTextFormatter(new ExtractedTextFormatter.Builder()
                        .withNumberOfBottomTextLinesToDelete(0)
                        .withNumberOfTopPagesToSkipBeforeDelete(0)
                        .build())
                .withPagesPerDocument(1)
                .build();
        var pdfReader = new PagePdfDocumentReader(pdfResource, config);
        return pdfReader.get();
    }

    private List<Document> getEpubDocumentContent(org.springframework.core.io.Resource epubResource) throws IOException {
        InputStream epubInputStream = new FileInputStream(epubResource.getFile());
        EpubReader epubReader = new EpubReader();
        Book book = epubReader.readEpub(epubInputStream);

        String text = book.getContents().stream()
                .map(ReferenceDocsLoader::readResourceAsString)
                .collect(Collectors.joining("\n"));
        return List.of(new Document(text));
    }

    private static String readResourceAsString(Resource resource) {
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(resource.getInputStream(), resource.getInputEncoding()));
            return reader.lines().collect(Collectors.joining("\n"));
        } catch (Exception e) {
            logger.info("Błąd przy pobieraniu zasobów jako String: ", e);
            throw new RuntimeException(e);
        }
    }

    @PostConstruct
    public void init() throws IOException {
        Integer count = jdbcClient.sql("SELECT COUNT(*) FROM vector_store")
                .query(Integer.class)
                .single();

        logger.info("Loaded {} vectors from the vector store", count);
        if (count == 0) {
            logger.info("Loading vectors from the vector store");
            var textSplitter = new TokenTextSplitter();
//            vectorStore.accept(textSplitter.apply(getPdfDocumentContent(pdfResource)));
            vectorStore.accept(textSplitter.apply(getEpubDocumentContent(epubResource)));
            logger.info("Loaded vectors from the vector store");
        }
    }

}
