package com.example.SpringDocumentationAI.readers;

import com.example.SpringDocumentationAI.ReferenceDocsLoader;
import nl.siegmann.epublib.domain.Book;
import nl.siegmann.epublib.epub.EpubReader;
import org.springframework.ai.document.Document;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.stream.Collectors;

public class EpubDocumentContent implements ReaderDocumentInterface {

    @Override
    public List<Document> getDocumentContent(org.springframework.core.io.Resource pdfResource) throws IOException {
        InputStream epubInputStream = new FileInputStream(pdfResource.getFile());
        EpubReader epubReader = new EpubReader();
        Book book = epubReader.readEpub(epubInputStream);

        String text = book.getContents().stream()
                .map(ReferenceDocsLoader::readResourceAsString)
                .collect(Collectors.joining("\n"));
        return List.of(new Document(text));
    }

    @Override
    public String getDocumentType() {
        return ".epub";
    }


}
