package br.goes.luis.application.core.infrastructure.config;

import com.google.auth.oauth2.GoogleCredentials;
import dev.langchain4j.model.chat.ChatModel;
import dev.langchain4j.model.chat.StreamingChatModel;
import dev.langchain4j.model.vertexai.gemini.HarmCategory;
import dev.langchain4j.model.vertexai.gemini.SafetyThreshold;
import dev.langchain4j.model.vertexai.gemini.VertexAiGeminiChatModel;
import dev.langchain4j.model.vertexai.gemini.VertexAiGeminiStreamingChatModel;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;
import java.util.Map;

import static dev.langchain4j.model.vertexai.gemini.HarmCategory.*;
import static dev.langchain4j.model.vertexai.gemini.SafetyThreshold.*;

@Configuration
public class ChatModelConfig {

    @Value("${vertex.project.id}")
    private String projectId;

    @Value("${vertex.gemini.location}")
    private String location;

    @Value("${vertex.gemini.model.name}")
    private String modelName;

    @Bean
    public ChatModel chatModel() throws IOException {
        return VertexAiGeminiChatModel.builder()
                .project(projectId)
                .location(location)
                .modelName(modelName)
                .credentials(GoogleCredentials.getApplicationDefault())
                .safetySettings(getSafetySettings())
                .build();
    }

    @Bean
    public StreamingChatModel streamingChatModel() throws IOException {
        return VertexAiGeminiStreamingChatModel.builder()
                .project(projectId)
                .location(location)
                .modelName(modelName)
                .credentials(GoogleCredentials.getApplicationDefault())
                .safetySettings(getSafetySettings())
                .build();
    }

    private Map<HarmCategory, SafetyThreshold> getSafetySettings() {
        return Map.of(
                HARM_CATEGORY_HARASSMENT, BLOCK_LOW_AND_ABOVE,
                HARM_CATEGORY_DANGEROUS_CONTENT, BLOCK_ONLY_HIGH,
                HARM_CATEGORY_SEXUALLY_EXPLICIT, BLOCK_MEDIUM_AND_ABOVE
        );
    }

}