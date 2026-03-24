package br.goes.luis.application.core.infrastructure.ai.adapter;

import com.google.auth.oauth2.GoogleCredentials;
import dev.langchain4j.data.embedding.Embedding;
import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.model.embedding.EmbeddingModel;
import dev.langchain4j.model.output.Response;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.io.IOException;
import java.net.URI;
import java.util.List;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class QwenVertexEmbeddingModelAdapter implements EmbeddingModel {

    @Value("${vertex.project.id}")
    private String projectId;

    @Value("${vertex.embedding.location}")
    private String location;

    @Value("${vertex.embedding.endpoint.id}")
    private String endpointId;

    @Value("${vertex.embedding.dedicated.endpoint}")
    private String dedicatedEndpoint;

    private final RestClient restClient;

    private GoogleCredentials credentials;
    private URI predictionUri;

    @PostConstruct
    public void init() throws IOException {
        this.credentials = GoogleCredentials.getApplicationDefault();
        this.predictionUri = URI.create(String.format("https://%s/v1/projects/%s/locations/%s/endpoints/%s:predict",
                dedicatedEndpoint, projectId, location, endpointId));
    }

    @Override
    public Response<List<Embedding>> embedAll(List<TextSegment> textSegments) {
        List<Map<String, String>> instances = textSegments.stream()
                .map(segment -> Map.of("inputs", segment.text()))
                .toList();

        Map<String, Object> response = restClient.post()
                .uri(predictionUri)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + getValidToken())
                .contentType(MediaType.APPLICATION_JSON)
                .body(Map.of("instances", instances))
                .retrieve()
                .body(new ParameterizedTypeReference<>() {});

        if (response == null || !response.containsKey("predictions")) {
            throw new IllegalStateException("A resposta do Vertex AI retornou vazia ou em formato inválido.");
        }

        return Response.from(parseEmbeddings(response));
    }

    @SuppressWarnings("unchecked")
    private List<Embedding> parseEmbeddings(Map<String, Object> response) {
        List<List<List<Number>>> predictions = (List<List<List<Number>>>) response.get("predictions");

        return predictions.stream().map(prediction -> {
            List<Number> vectorData = prediction.getFirst();
            float[] vector = new float[vectorData.size()];
            for (int i = 0; i < vectorData.size(); i++) {
                vector[i] = vectorData.get(i).floatValue();
            }
            return new Embedding(vector);
        }).toList();
    }

    private String getValidToken() {
        try {
            credentials.refreshIfExpired();
            return credentials.getAccessToken().getTokenValue();
        } catch (IOException e) {
            throw new RuntimeException("Falha ao obter credenciais do GCP", e);
        }
    }

}