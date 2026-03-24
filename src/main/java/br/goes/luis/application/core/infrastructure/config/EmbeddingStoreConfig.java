package br.goes.luis.application.core.infrastructure.config;

import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.model.embedding.EmbeddingModel;
import dev.langchain4j.store.embedding.EmbeddingStore;
import dev.langchain4j.store.embedding.pgvector.PgVectorEmbeddingStore;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;

@Configuration
public class EmbeddingStoreConfig {

    @Bean
    public EmbeddingStore<TextSegment> embeddingStore(DataSource dataSource, EmbeddingModel embeddingModel) {
        return PgVectorEmbeddingStore.datasourceBuilder()
                .datasource(dataSource)
                .dimension(embeddingModel.dimension())
                .table("document_chunks")
                .searchMode(PgVectorEmbeddingStore.SearchMode.HYBRID)
                .createTable(true)
                .dropTableFirst(false)
                .rrfK(60)
                .build();
    }

}