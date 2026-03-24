package br.goes.luis.application.core.infrastructure.service.impl;

import br.goes.luis.application.core.infrastructure.service.GoogleBucketStorageService;
import com.google.cloud.storage.Blob;
import com.google.cloud.storage.BlobId;
import com.google.cloud.storage.BlobInfo;
import com.google.cloud.storage.Storage;
import org.jspecify.annotations.NonNull;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class GoogleBucketStorageServiceImpl implements GoogleBucketStorageService {

    private final String bucketName;
    private final Storage storage;

    public GoogleBucketStorageServiceImpl(@Value("${google.bucket.name}") String bucketName, Storage storage) {
        this.bucketName = bucketName;
        this.storage = storage;
    }

    @Override
    public Blob getContent(BlobId blobId) {
        return storage.get(blobId);
    }

    @Override
    public Blob upload(String originalFilename, String mimeType, byte[] fileBytes) {
        String uniqueFilename = UUID.randomUUID() + "-" + originalFilename;
        BlobId blobId = BlobId.of(bucketName, uniqueFilename);

        BlobInfo blobInfo = BlobInfo.newBuilder(blobId)
                .setContentType(mimeType)
                .build();

        return storage.create(blobInfo, fileBytes);
    }

    @Override
    public Blob update(@NonNull BlobId blobId, String mimeType, byte[] fileBytes) {
        BlobInfo blobInfo = BlobInfo.newBuilder(blobId)
                .setContentType(mimeType)
                .build();

        return storage.create(blobInfo, fileBytes);
    }

    @Override
    public void delete(@NonNull BlobId blobId) {
        storage.delete(blobId);
    }

}