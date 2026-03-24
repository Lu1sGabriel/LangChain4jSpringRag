package br.goes.luis.application.core.infrastructure.ai.service;

import dev.langchain4j.invocation.InvocationParameters;
import dev.langchain4j.service.MemoryId;
import dev.langchain4j.service.SystemMessage;
import dev.langchain4j.service.UserMessage;
import dev.langchain4j.service.spring.AiService;
import dev.langchain4j.service.spring.AiServiceWiringMode;
import reactor.core.publisher.Flux;

import java.util.UUID;

@AiService(
        wiringMode = AiServiceWiringMode.EXPLICIT,
        streamingChatModel = "streamingChatModel",
        chatMemoryProvider = "chatMemoryProvider",
        contentRetriever = "dynamicRoutingRetriever"
)
public interface AiChatService {

    @SystemMessage("""
            Você é um assistente de IA direto e objetivo.
            Sua única função é responder com base no contexto extraído dos documentos fornecidos.
            Se a informação não estiver no contexto, diga que não encontrou.
            Responda no mesmo idioma do usuário.
            """)
    Flux<String> chat(
            @MemoryId UUID chatId,
            @UserMessage String message,
            InvocationParameters parameters
    );

}