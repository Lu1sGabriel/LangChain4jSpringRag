package br.goes.luis.application.modules.chat.application.useCase;

import br.goes.luis.application.modules.chat.presentation.dto.request.ChatCreateRequestDto;

import java.util.UUID;

public interface ChatCreateUseCase {
    UUID create(ChatCreateRequestDto requestDto);
}