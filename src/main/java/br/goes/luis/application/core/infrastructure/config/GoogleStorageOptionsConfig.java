package br.goes.luis.application.core.infrastructure.config;

import com.google.cloud.storage.Storage;
import com.google.cloud.storage.StorageOptions;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class GoogleStorageOptionsConfig {

    @Bean
    public Storage googleStorage() {
        return StorageOptions.getDefaultInstance().getService();
    }

}