package br.goes.luis.application.modules.chat.application.useCase.impl;

import br.goes.luis.application.modules.chat.application.useCase.ChatCreateUseCase;
import br.goes.luis.application.modules.chat.domain.entity.ChatEntity;
import br.goes.luis.application.modules.chat.infrastructure.repository.ChatRepository;
import br.goes.luis.application.modules.chat.presentation.dto.request.ChatCreateRequestDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ChatCreateImpl implements ChatCreateUseCase {

    private final ChatRepository chatRepository;

    @Override
    public UUID create(ChatCreateRequestDto requestDto) {
        var chat = new ChatEntity(requestDto.name());
        var savedChat = chatRepository.save(chat);
        return savedChat.getId();
    }

}