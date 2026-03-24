package br.goes.luis.application.modules.document.domain.entity;

import br.goes.luis.application.core.shared.domain.entity.BaseEntity;
import br.goes.luis.application.modules.document.domain.enumeration.DocumentChunkStatusEnum;
import br.goes.luis.application.modules.document.domain.enumeration.DocumentTypeEnum;
import br.goes.luis.application.modules.document.domain.enumeration.DocumentUploadStatusEnum;
import com.google.cloud.storage.BlobId;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "documents", uniqueConstraints = {
        @UniqueConstraint(name = "uk_gcs_location", columnNames = {"gcs_bucket", "gcs_object_name"})
})
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class DocumentEntity extends BaseEntity {

    @Column(name = "file_name", nullable = false)
    private String fileName;

    @Column(name = "blob_id", unique = true)
    private BlobId blobId;

    @Column(name = "gcs_bucket")
    private String gcsBucket;

    @Column(name = "gcs_object_name")
    private String gcsObjectName;

    @Column(name = "mime_type", nullable = false)
    private String mimeType;

    @Column(name = "size_bytes")
    private Long sizeBytes;

    @Column(name = "hash", unique = true, nullable = false, updatable = false)
    private String hash;

    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false)
    private DocumentTypeEnum type;

    @Enumerated(EnumType.STRING)
    @Column(name = "chunk_status", nullable = false)
    private DocumentChunkStatusEnum chunkStatus;

    @Enumerated(EnumType.STRING)
    @Column(name = "upload_status", nullable = false)
    private DocumentUploadStatusEnum uploadStatus;

    public DocumentEntity(String fileName, String mimeType, Long sizeBytes, String hash, DocumentTypeEnum type) {
        this.fileName = fileName;
        this.mimeType = mimeType;
        this.sizeBytes = sizeBytes;
        this.hash = hash;
        this.type = type;
        this.chunkStatus = DocumentChunkStatusEnum.PROCESSING;
        this.uploadStatus = DocumentUploadStatusEnum.PROCESSING;
    }

    public void addGoogleStorageBucketData(BlobId blobId, String gcsBucket, String gcsObjectName) {
        this.blobId = blobId;
        this.gcsBucket = gcsBucket;
        this.gcsObjectName = gcsObjectName;
    }

    public void markChunkAsCompleted() {
        this.chunkStatus = DocumentChunkStatusEnum.COMPLETED;
    }

    public void markChunkAsError() {
        this.chunkStatus = DocumentChunkStatusEnum.ERROR;
    }

    public void markUploadAsCompleted() {
        this.uploadStatus = DocumentUploadStatusEnum.COMPLETED;
    }

    public void markUploadAsError() {
        this.uploadStatus = DocumentUploadStatusEnum.ERROR;
    }

}