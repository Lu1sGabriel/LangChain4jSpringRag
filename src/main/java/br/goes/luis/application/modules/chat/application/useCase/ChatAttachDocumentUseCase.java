package br.goes.luis.application.modules.chat.application.useCase;

import br.goes.luis.application.modules.chat.presentation.dto.request.ChatAttachDocumentRequestDto;

import java.util.UUID;

public interface ChatAttachDocumentUseCase {
    void attach(UUID chatId, ChatAttachDocumentRequestDto requestDto);
}