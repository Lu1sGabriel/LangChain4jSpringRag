CREATE TABLE documents
(
    id              UUID PRIMARY KEY,
    file_name       VARCHAR(255)             NOT NULL,
    blob_id         BYTEA UNIQUE,
    gcs_bucket      VARCHAR(255),
    gcs_object_name VARCHAR(255),
    mime_type       VARCHAR(255)             NOT NULL,
    size_bytes      BIGINT,
    hash            VARCHAR(255)             NOT NULL UNIQUE,
    type            VARCHAR(50)              NOT NULL,
    chunk_status    VARCHAR(50)              NOT NULL,
    upload_status   VARCHAR(50)              NOT NULL,
    created_at      TIMESTAMP WITH TIME ZONE NOT NULL,
    updated_at      TIMESTAMP WITH TIME ZONE NOT NULL,
    deleted_at      TIMESTAMP WITH TIME ZONE,

    CONSTRAINT uk_gcs_location UNIQUE (gcs_bucket, gcs_object_name)
);

CREATE INDEX idx_documents_hash ON documents (hash);
CREATE INDEX idx_documents_status ON documents (chunk_status, upload_status);
CREATE INDEX idx_documents_deleted_at ON documents (deleted_at);