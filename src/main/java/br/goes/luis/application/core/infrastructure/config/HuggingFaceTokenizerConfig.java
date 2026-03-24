package br.goes.luis.application.core.infrastructure.config;

import ai.djl.huggingface.tokenizers.HuggingFaceTokenizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

@Configuration
public class HuggingFaceTokenizerConfig {

    @Bean
    public HuggingFaceTokenizer huggingFaceTokenizer() throws IOException {
        try (InputStream is = getClass().getResourceAsStream("/qwen-tokenizer.json")) {
            if (is == null) {
                throw new FileNotFoundException("qwen-tokenizer.json not found in classpath.");
            }

            Map<String, String> options = Map.of("maxLength", "8192");

            return HuggingFaceTokenizer.newInstance(is, options);
        }
    }

}