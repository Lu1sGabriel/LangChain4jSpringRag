package br.goes.luis.application.modules.document.infrastructure.service;

import br.goes.luis.application.modules.document.domain.entity.DocumentEntity;

public interface DocumentAsyncProcessorService {
    void process(DocumentEntity document, byte[] fileBytes, String originalFilename, String mimeType);
}