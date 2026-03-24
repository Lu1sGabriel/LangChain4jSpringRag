package br.goes.luis.application.modules.document.infrastructure.service.impl;

import br.goes.luis.application.core.infrastructure.service.GoogleBucketStorageService;
import br.goes.luis.application.modules.document.domain.entity.DocumentEntity;
import br.goes.luis.application.modules.document.infrastructure.repository.DocumentRepository;
import br.goes.luis.application.modules.document.infrastructure.service.DocumentAsyncProcessorService;
import br.goes.luis.application.modules.rag.infrastructure.service.RagService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class DocumentAsyncProcessorImpl implements DocumentAsyncProcessorService {

    private final DocumentRepository documentRepository;
    private final GoogleBucketStorageService googleBucketStorageService;
    private final RagService ragService;

    @Override
    @Async
    public void process(DocumentEntity document, byte[] fileBytes, String originalFilename, String mimeType) {
        try {
            var blob = googleBucketStorageService.upload(originalFilename, mimeType, fileBytes);

            document.addGoogleStorageBucketData(blob.getBlobId(), blob.getBucket(), blob.getName());
            document.markUploadAsCompleted();
            document = documentRepository.save(document);

            ragService.process(fileBytes, document);
            document.markChunkAsCompleted();

        } catch (Exception e) {
            document.markUploadAsError();
            document.markChunkAsError();
            throw new RuntimeException("Falha no processamento em background do documento", e);
        } finally {
            documentRepository.save(document);
        }
    }

}