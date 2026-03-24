package br.goes.luis.application.core.infrastructure.ai.retriever;

import br.goes.luis.application.modules.document.domain.enumeration.DocumentTypeEnum;
import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.model.embedding.EmbeddingModel;
import dev.langchain4j.rag.content.Content;
import dev.langchain4j.rag.content.retriever.ContentRetriever;
import dev.langchain4j.rag.content.retriever.EmbeddingStoreContentRetriever;
import dev.langchain4j.rag.query.Query;
import dev.langchain4j.store.embedding.EmbeddingStore;
import dev.langchain4j.store.embedding.filter.Filter;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import static dev.langchain4j.store.embedding.filter.MetadataFilterBuilder.metadataKey;

@Component("dynamicRoutingRetriever")
public class DynamicRoutingRetriever implements ContentRetriever {

    private final EmbeddingStoreContentRetriever delegateRetriever;

    public DynamicRoutingRetriever(EmbeddingStore<TextSegment> embeddingStore, EmbeddingModel embeddingModel) {
        this.delegateRetriever = EmbeddingStoreContentRetriever.builder()
                .embeddingStore(embeddingStore)
                .embeddingModel(embeddingModel)
                .maxResults(5)
                .dynamicFilter(this::createFilter)
                .build();
    }

    @Override
    public List<Content> retrieve(Query query) {
        return delegateRetriever.retrieve(query);
    }

    @SuppressWarnings("unchecked")
    private Filter createFilter(Query query) {
        Map<String, Object> params = query.metadata().invocationParameters().asMap();

        if (params != null && params.containsKey("documentId")) {
            Object documentIdsObj = params.get("documentId");
            if (documentIdsObj instanceof List<?> list && !list.isEmpty() && list.getFirst() instanceof UUID) {
                List<String> idsAsString = ((List<UUID>) list).stream().map(UUID::toString).toList();
                return metadataKey("document_id").isIn(idsAsString);
            }
        }

        return metadataKey("document_type").isEqualTo(DocumentTypeEnum.FAQ.getName());
    }

}