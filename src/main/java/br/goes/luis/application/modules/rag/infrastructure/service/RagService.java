package br.goes.luis.application.modules.rag.infrastructure.service;

import br.goes.luis.application.core.infrastructure.ai.service.DocumentEnricherService;
import br.goes.luis.application.core.infrastructure.service.PdfExtractionService;
import br.goes.luis.application.modules.document.domain.entity.DocumentEntity;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import dev.langchain4j.data.document.Document;
import dev.langchain4j.store.embedding.EmbeddingStoreIngestor;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.NonNull;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executors;

@Service
@RequiredArgsConstructor
public class RagService {

    private final EmbeddingStoreIngestor embeddingStoreIngestor;
    private final PdfExtractionService pdfExtractionService;
    private final DocumentEnricherService documentEnricherService;
    private final ObjectMapper objectMapper;

    public void process(byte[] fileBytes, @NonNull DocumentEntity documentEntity) {
        List<Document> documents = pdfExtractionService.extract(fileBytes, documentEntity.getMimeType());

        try (var executor = Executors.newVirtualThreadPerTaskExecutor()) {
            var futures = documents.stream()
                    .filter(doc -> doc.text() != null && !doc.text().isBlank())
                    .map(document -> CompletableFuture.runAsync(() -> enrichDocument(document, documentEntity), executor))
                    .toArray(CompletableFuture[]::new);

            CompletableFuture.allOf(futures).join();
        }

        embeddingStoreIngestor.ingest(documents);
    }

    private void enrichDocument(Document document, DocumentEntity documentEntity) {
        document.metadata().put("document_id", documentEntity.getId().toString());
        document.metadata().put("document_type", documentEntity.getType().getName());

        var metadataRecord = documentEnricherService.extract(document.text());
        Map<String, Object> metadataMap = objectMapper.convertValue(metadataRecord, new TypeReference<>() {
        });

        metadataMap.forEach((key, value) -> {
            if (value != null) {
                document.metadata().put(key, value.toString());
            }
        });
    }

}