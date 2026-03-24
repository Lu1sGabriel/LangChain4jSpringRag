package br.goes.luis.application.modules.document.application.useCase.impl;

import br.goes.luis.application.modules.document.application.useCase.DocumentUploadUseCase;
import br.goes.luis.application.modules.document.domain.entity.DocumentEntity;
import br.goes.luis.application.modules.document.domain.enumeration.DocumentTypeEnum;
import br.goes.luis.application.modules.document.infrastructure.repository.DocumentRepository;
import br.goes.luis.application.modules.document.infrastructure.service.DocumentAsyncProcessorService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.DigestUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.UncheckedIOException;

@Service
@RequiredArgsConstructor
public class DocumentUploadImpl implements DocumentUploadUseCase {

    private final DocumentRepository documentRepository;
    private final DocumentAsyncProcessorService documentAsyncProcessorService;

    @Override
    @Transactional
    public void uploadPrivateDocument(MultipartFile multipartFile) {
        upload(multipartFile, DocumentTypeEnum.PRIVATE);
    }

    @Override
    @Transactional
    public void uploadFaqDocument(MultipartFile multipartFile) {
        upload(multipartFile, DocumentTypeEnum.FAQ);
    }

    private void upload(MultipartFile multipartFile, DocumentTypeEnum documentType) {
        validateFile(multipartFile);

        byte[] fileBytes = extractBytes(multipartFile);
        String hash = DigestUtils.md5DigestAsHex(fileBytes);

        verifyDuplicate(hash);

        var document = documentRepository.save(new DocumentEntity(
                multipartFile.getOriginalFilename(),
                multipartFile.getContentType(),
                multipartFile.getSize(),
                hash,
                documentType
        ));

        documentAsyncProcessorService.process(document, fileBytes, multipartFile.getOriginalFilename(), multipartFile.getContentType());
    }

    private void validateFile(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("O arquivo está nulo ou vazio");
        }
    }

    private void verifyDuplicate(String hash) {
        if (documentRepository.existsByHash(hash)) {
            throw new IllegalStateException("O documento enviado já existe no banco de dados.");
        }
    }

    private byte[] extractBytes(MultipartFile file) {
        try {
            return file.getBytes();
        } catch (IOException e) {
            throw new UncheckedIOException("Falha ao ler bytes do arquivo", e);
        }
    }

}