package br.goes.luis.application.modules.document.application.useCase;

import org.springframework.web.multipart.MultipartFile;

public interface DocumentUploadUseCase {
    void uploadPrivateDocument(MultipartFile multipartFile);

    void uploadFaqDocument(MultipartFile multipartFile);
}