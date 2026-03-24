package br.goes.luis.application.core.infrastructure.ai.service;

import dev.langchain4j.service.SystemMessage;
import dev.langchain4j.service.UserMessage;
import dev.langchain4j.service.spring.AiService;

import java.util.List;

@AiService(contentRetriever = "none")
public interface DocumentEnricherService {

    @SystemMessage("""
            You are a data enrichment specialist. Analyze the provided text and extract metadata.
            Return ONLY valid JSON.
            """)
    DocumentMetadata extract(@UserMessage String text);

    record DocumentMetadata(
            String main_topic,
            List<String> entities,
            List<String> key_words,
            Float sentiment_score,
            Integer priority_level,
            String reference_date,
            String language_code,
            String summary_short
    ) {
    }

}