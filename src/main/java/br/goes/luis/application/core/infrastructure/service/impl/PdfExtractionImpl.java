package br.goes.luis.application.core.infrastructure.service.impl;

import br.goes.luis.application.core.infrastructure.service.PdfExtractionService;
import com.google.cloud.documentai.v1.DocumentProcessorServiceClient;
import com.google.cloud.documentai.v1.ProcessRequest;
import com.google.cloud.documentai.v1.RawDocument;
import com.google.protobuf.ByteString;
import dev.langchain4j.data.document.Document;
import dev.langchain4j.data.document.Metadata;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Service
public class PdfExtractionImpl implements PdfExtractionService {

    @Value("${vertex.documentAi.project.id}")
    private String projectId;

    @Value("${vertex.documentAi.location}")
    private String location;

    @Value("${vertex.documentAi.processor.id}")
    private String processorId;

    private DocumentProcessorServiceClient client;

    @PostConstruct
    public void init() throws IOException {
        this.client = DocumentProcessorServiceClient.create();
    }

    @PreDestroy
    public void close() {
        if (this.client != null) {
            this.client.close();
        }
    }

    @Override
    public List<Document> extract(byte[] bytes, String mimeType) {
        String name = String.format("projects/%s/locations/%s/processors/%s", projectId, location, processorId);

        ProcessRequest request = ProcessRequest.newBuilder()
                .setName(name)
                .setRawDocument(RawDocument.newBuilder().setContent(ByteString.copyFrom(bytes)).setMimeType(mimeType).build())
                .build();

        var gcpDoc = client.processDocument(request).getDocument();
        String fullText = gcpDoc.getText();
        List<Document> langchainDocuments = new ArrayList<>();

        for (var page : gcpDoc.getPagesList()) {
            String pageText = extractTextSegment(page.getLayout().getTextAnchor(), fullText);
            String tablesMarkdown = extractTablesAsMarkdown(page.getTablesList(), fullText);

            Metadata metadata = new Metadata();
            metadata.put("Page", page.getPageNumber());
            metadata.put("HasTables", String.valueOf(!page.getTablesList().isEmpty()));

            langchainDocuments.add(Document.document((pageText + "\n" + tablesMarkdown).trim(), metadata));
        }

        return langchainDocuments;
    }

    private String extractTablesAsMarkdown(List<com.google.cloud.documentai.v1.Document.Page.Table> tables, String fullText) {
        if (tables.isEmpty()) return "";

        StringBuilder sb = new StringBuilder("\n--- Tabelas Estruturadas ---\n");
        for (var table : tables) {
            for (var row : table.getHeaderRowsList()) {
                sb.append(formatRow(row, fullText)).append("\n");
                sb.append("|---".repeat(Math.max(1, row.getCellsCount()))).append("|\n");
            }
            for (var row : table.getBodyRowsList()) {
                sb.append(formatRow(row, fullText)).append("\n");
            }
            sb.append("\n");
        }
        return sb.toString();
    }

    private String formatRow(com.google.cloud.documentai.v1.Document.Page.Table.TableRow row, String fullText) {
        StringBuilder rowText = new StringBuilder("| ");
        for (var cell : row.getCellsList()) {
            String cellText = extractTextSegment(cell.getLayout().getTextAnchor(), fullText).replace("\n", " ").trim();
            rowText.append(cellText).append(" | ");
        }
        return rowText.toString();
    }

    private String extractTextSegment(com.google.cloud.documentai.v1.Document.TextAnchor textAnchor, String fullText) {
        StringBuilder text = new StringBuilder();
        for (var segment : textAnchor.getTextSegmentsList()) {
            int start = (int) segment.getStartIndex();
            int end = (int) segment.getEndIndex();
            if (start < end && end <= fullText.length()) {
                text.append(fullText, start, end);
            }
        }
        return text.toString();
    }

}