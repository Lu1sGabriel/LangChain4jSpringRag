package br.goes.luis.application.modules.chat.application.useCase;

import br.goes.luis.application.modules.chat.presentation.dto.request.ChatRequestDto;
import reactor.core.publisher.Flux;

import java.util.UUID;

public interface ChatDoChatUseCase {
    Flux<String> doChat(UUID chatId, ChatRequestDto chatRequestDto);
}