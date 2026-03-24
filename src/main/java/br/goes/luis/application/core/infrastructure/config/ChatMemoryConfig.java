package br.goes.luis.application.core.infrastructure.config;

import br.goes.luis.application.core.infrastructure.ai.provider.JpaChatMemoryStoreProvider;
import dev.langchain4j.memory.chat.ChatMemoryProvider;
import dev.langchain4j.memory.chat.MessageWindowChatMemory;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class ChatMemoryConfig {

    @Bean
    public ChatMemoryProvider chatMemoryProvider(JpaChatMemoryStoreProvider store) {
        return chatId -> MessageWindowChatMemory.builder()
                .id(chatId)
                .maxMessages(10)
                .chatMemoryStore(store)
                .build();
    }

}