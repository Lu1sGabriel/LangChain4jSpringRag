package br.goes.luis.application.core.infrastructure.ai.provider;

import br.goes.luis.application.modules.chat.domain.entity.ChatMessageEntity;
import br.goes.luis.application.modules.chat.infrastructure.repository.ChatRepository;
import dev.langchain4j.data.message.AiMessage;
import dev.langchain4j.data.message.ChatMessage;
import dev.langchain4j.data.message.SystemMessage;
import dev.langchain4j.data.message.UserMessage;
import dev.langchain4j.store.memory.chat.ChatMemoryStore;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class JpaChatMemoryStoreProvider implements ChatMemoryStore {

    private final ChatRepository chatRepository;

    @Override
    @Transactional(readOnly = true)
    public List<ChatMessage> getMessages(Object memoryId) {
        UUID chatId = (UUID) memoryId;

        var chat = chatRepository.findByIdAndActivateOrThrow(chatId);

        return chat.getChatMessages()
                .stream()
                .map(this::toLangChainMessage)
                .toList();
    }

    @Override
    @Transactional
    public void updateMessages(Object memoryId, List<ChatMessage> messages) {
        UUID chatId = (UUID) memoryId;

        var chat = chatRepository.findByIdAndActivateOrThrow(chatId);
        chat.getChatMessages().clear();

        messages.forEach(message -> chat.addMessage(new ChatMessageEntity(chat, extractText(message), message.type())));

        chatRepository.save(chat);
    }

    @Override
    public void deleteMessages(Object memoryId) {
        throw new UnsupportedOperationException("Método não implementado");
    }

    private String extractText(ChatMessage message) {
        return switch (message) {
            case UserMessage um -> um.hasSingleText() ? um.singleText() : "";
            case AiMessage am -> am.text() != null ? am.text() : "";
            case null, default -> "";
        };
    }

    private ChatMessage toLangChainMessage(ChatMessageEntity entity) {
        return switch (entity.getChatMessageType()) {
            case USER -> new UserMessage(entity.getMessage());
            case AI -> new AiMessage(entity.getMessage());
            case SYSTEM -> new SystemMessage(entity.getMessage());
            default ->
                    throw new IllegalArgumentException("Tipo de mensagem não suportado: " + entity.getChatMessageType());
        };
    }

}