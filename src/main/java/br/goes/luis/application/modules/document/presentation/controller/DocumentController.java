package br.goes.luis.application.modules.document.presentation.controller;

import br.goes.luis.application.modules.document.application.useCase.DocumentUploadUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping(value = "/api/v1/documents")
@RequiredArgsConstructor
public class DocumentController {

    private final DocumentUploadUseCase uploadUseCase;

    @PostMapping(value = "/upload/private", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Void> uploadDocument(@RequestParam("file") MultipartFile file) {
        uploadUseCase.uploadPrivateDocument(file);
        return ResponseEntity.accepted().build();
    }

    @PostMapping(value = "/upload/faq", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Void> uploadFaqDocument(@RequestParam("file") MultipartFile file) {
        uploadUseCase.uploadFaqDocument(file);
        return ResponseEntity.accepted().build();
    }

}