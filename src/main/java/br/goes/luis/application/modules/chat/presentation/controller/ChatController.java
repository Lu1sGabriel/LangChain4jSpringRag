package br.goes.luis.application.modules.chat.presentation.controller;

import br.goes.luis.application.modules.chat.application.useCase.ChatCreateUseCase;
import br.goes.luis.application.modules.chat.application.useCase.ChatDoChatUseCase;
import br.goes.luis.application.modules.chat.presentation.dto.request.ChatCreateRequestDto;
import br.goes.luis.application.modules.chat.presentation.dto.request.ChatRequestDto;
import lombok.RequiredArgsConstructor;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;

import java.util.UUID;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RestController
@RequestMapping(value = "/api/v1/chats")
@RequiredArgsConstructor
public class ChatController {

    private final ChatCreateUseCase createUseCase;
    private final ChatDoChatUseCase doChatUseCase;

    @PostMapping
    public ResponseEntity<EntityModel<UUID>> create(@RequestBody ChatCreateRequestDto requestDto) {
        var chatId = createUseCase.create(requestDto);
        return ResponseEntity.ok(
                EntityModel.of(chatId,
                        linkTo(methodOn(ChatController.class).doChat(chatId, null)).withRel("do-chat")
                )
        );
    }

    @PostMapping(value = "/{chatId}", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public ResponseEntity<Flux<String>> doChat(@PathVariable UUID chatId, @RequestBody ChatRequestDto requestDto) {
        return ResponseEntity.ok(doChatUseCase.doChat(chatId, requestDto));
    }

}