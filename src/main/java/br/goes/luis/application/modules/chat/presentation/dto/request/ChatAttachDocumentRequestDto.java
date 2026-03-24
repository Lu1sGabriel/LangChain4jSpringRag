package br.goes.luis.application.modules.chat.presentation.dto.request;

import java.util.List;
import java.util.UUID;

public record ChatAttachDocumentRequestDto(
        List<UUID> documentsIds
) {
}