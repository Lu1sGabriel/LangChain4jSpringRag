package br.goes.luis.application.modules.chat.application.useCase.impl;

import br.goes.luis.application.modules.chat.application.useCase.ChatAttachDocumentUseCase;
import br.goes.luis.application.modules.chat.infrastructure.repository.ChatRepository;
import br.goes.luis.application.modules.chat.presentation.dto.request.ChatAttachDocumentRequestDto;
import br.goes.luis.application.modules.document.infrastructure.repository.DocumentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ChatAttachDocumentImpl implements ChatAttachDocumentUseCase {

    private final DocumentRepository documentRepository;
    private final ChatRepository chatRepository;

    @Override
    public void attach(UUID chatId, ChatAttachDocumentRequestDto requestDto) {
        if (chatId == null || requestDto.documentsIds().isEmpty()) {
            return;
        }

        var chat = chatRepository.findByIdAndActivateOrThrow(chatId);
        var documents = documentRepository.findAllActiveById(requestDto.documentsIds());

        chat.addDocument(documents);
        chatRepository.save(chat);
    }

}