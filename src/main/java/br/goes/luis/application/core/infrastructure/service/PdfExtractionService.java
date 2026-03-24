package br.goes.luis.application.core.infrastructure.service;

import dev.langchain4j.data.document.Document;

import java.util.List;

public interface PdfExtractionService {
    List<Document> extract(byte[] bytes, String mimeType);
}