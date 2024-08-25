package com.example.SpringDocumentationAI.readers;

import org.springframework.ai.document.Document;

import java.io.IOException;
import java.util.List;

public interface ReaderDocumentInterface {
    List<Document> getDocumentContent(org.springframework.core.io.Resource pdfResource) throws IOException;
    String getDocumentType();
}
