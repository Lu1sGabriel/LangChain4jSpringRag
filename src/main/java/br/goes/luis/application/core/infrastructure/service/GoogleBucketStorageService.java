package br.goes.luis.application.core.infrastructure.service;

import com.google.cloud.storage.Blob;
import com.google.cloud.storage.BlobId;

public interface GoogleBucketStorageService {
    Blob getContent(BlobId blobId);

    Blob upload(String originalFilename, String mimeType, byte[] fileBytes);

    Blob update(BlobId blobId, String mimeType, byte[] fileBytes);

    void delete(BlobId blobId);
}