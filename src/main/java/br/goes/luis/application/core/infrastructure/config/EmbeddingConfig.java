package br.goes.luis.application.core.infrastructure.config;

import br.goes.luis.application.core.infrastructure.ai.adapter.QwenVertexEmbeddingModelAdapter;
import br.goes.luis.application.core.infrastructure.ai.adapter.QwenVertexEmbeddingTokenizerAdapter;
import dev.langchain4j.data.document.splitter.DocumentSplitters;
import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.model.embedding.EmbeddingModel;
import dev.langchain4j.store.embedding.EmbeddingStore;
import dev.langchain4j.store.embedding.EmbeddingStoreIngestor;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class EmbeddingConfig {

    private final static int MAX_TOKENS = 600;
    private final static int OVERLAP = 100;

    private final QwenVertexEmbeddingModelAdapter qwenVertexEmbeddingModelAdapter;

    @Bean
    public EmbeddingModel embeddingModel() {
        return qwenVertexEmbeddingModelAdapter;
    }

    @Bean
    public EmbeddingStoreIngestor embeddingStoreIngestor(EmbeddingModel embeddingModel,
                                                         EmbeddingStore<TextSegment> embeddingStore,
                                                         QwenVertexEmbeddingTokenizerAdapter qwenVertexEmbeddingTokenizerAdapter) {
        return EmbeddingStoreIngestor.builder()
                .embeddingModel(embeddingModel)
                .embeddingStore(embeddingStore)
                .documentSplitter(DocumentSplitters.recursive(MAX_TOKENS, OVERLAP, qwenVertexEmbeddingTokenizerAdapter))
                .build();
    }

}