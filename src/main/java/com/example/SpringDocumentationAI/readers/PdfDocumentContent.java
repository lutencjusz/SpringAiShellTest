package com.example.SpringDocumentationAI.readers;

import org.springframework.ai.document.Document;
import org.springframework.ai.reader.ExtractedTextFormatter;
import org.springframework.ai.reader.pdf.PagePdfDocumentReader;
import org.springframework.ai.reader.pdf.config.PdfDocumentReaderConfig;

import java.io.IOException;
import java.util.List;

public class PdfDocumentContent implements ReaderDocumentInterface {

    @Override
    public List<Document> getDocumentContent(org.springframework.core.io.Resource pdfResource) throws IOException {
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

    @Override
    public String getDocumentType() {
        return ".pdf";
    }
}
