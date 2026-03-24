package br.goes.luis.application.modules.chat.application.useCase.impl;

import br.goes.luis.application.core.infrastructure.ai.service.AiChatService;
import br.goes.luis.application.modules.chat.application.useCase.ChatDoChatUseCase;
import br.goes.luis.application.modules.chat.domain.entity.ChatDocumentEntity;
import br.goes.luis.application.modules.chat.infrastructure.repository.ChatRepository;
import br.goes.luis.application.modules.chat.presentation.dto.request.ChatRequestDto;
import br.goes.luis.application.modules.document.domain.entity.DocumentEntity;
import br.goes.luis.application.modules.document.domain.enumeration.DocumentTypeEnum;
import dev.langchain4j.invocation.InvocationParameters;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ChatDoChatImpl implements ChatDoChatUseCase {

    private final ChatRepository chatRepository;
    private final AiChatService aiChatService;

    @Override
    @Transactional(readOnly = true)
    public Flux<String> doChat(UUID chatId, ChatRequestDto chatRequestDto) {
        var chat = chatRepository.findByIdAndActivateOrThrow(chatId);

        List<UUID> documentsIds = chat.getChatDocuments().stream()
                .map(ChatDocumentEntity::getDocumentEntity)
                .map(DocumentEntity::getId)
                .toList();

        Map<String, Object> paramsMap = documentsIds.isEmpty()
                ? Map.of("documentTypes", List.of(DocumentTypeEnum.FAQ.name()))
                : Map.of("documentId", documentsIds);

        return aiChatService.chat(chatId, chatRequestDto.message(), new InvocationParameters(paramsMap));
    }

}