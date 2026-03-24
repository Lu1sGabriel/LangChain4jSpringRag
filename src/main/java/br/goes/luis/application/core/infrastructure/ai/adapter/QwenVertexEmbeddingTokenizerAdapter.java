package br.goes.luis.application.core.infrastructure.ai.adapter;

import ai.djl.huggingface.tokenizers.HuggingFaceTokenizer;
import dev.langchain4j.data.message.AiMessage;
import dev.langchain4j.data.message.ChatMessage;
import dev.langchain4j.data.message.SystemMessage;
import dev.langchain4j.data.message.UserMessage;
import dev.langchain4j.model.TokenCountEstimator;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class QwenVertexEmbeddingTokenizerAdapter implements TokenCountEstimator {

    private final HuggingFaceTokenizer huggingFaceTokenizer;

    private static final int TOKENS_PER_MESSAGE = 4;
    private static final int TOKENS_PER_REPLY = 3;

    @Override
    public int estimateTokenCountInText(String s) {
        if (s == null || s.isBlank()) {
            return 0;
        }
        return huggingFaceTokenizer.encode(s).getIds().length;
    }

    @Override
    public int estimateTokenCountInMessage(ChatMessage chatMessage) {
        String text = extractText(chatMessage);

        if (text.isBlank()) {
            return 0;
        }

        return estimateTokenCountInText(text) + TOKENS_PER_MESSAGE;
    }

    @Override
    public int estimateTokenCountInMessages(Iterable<ChatMessage> messages) {
        int totalTokens = 0;

        for (ChatMessage message : messages) {
            totalTokens += estimateTokenCountInMessage(message);
        }

        return totalTokens > 0 ? totalTokens + TOKENS_PER_REPLY : 0;
    }

    private String extractText(ChatMessage message) {
        return switch (message) {
            case SystemMessage sm -> sm.text();
            case UserMessage um -> um.hasSingleText() ? um.singleText() : "";
            case AiMessage am -> am.text() != null ? am.text() : "";
            case null, default -> "";
        };
    }

}