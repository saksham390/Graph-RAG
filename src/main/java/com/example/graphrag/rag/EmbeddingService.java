package com.example.graphrag.rag;

import dev.langchain4j.model.embedding.EmbeddingModel;
import dev.langchain4j.model.openai.OpenAiEmbeddingModel;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Arrays;

@Service
public class EmbeddingService {

    private static final int VECTOR_SIZE = 1536;
    private final EmbeddingModel openAiModel;

    public EmbeddingService(@Value("${openai.api-key:}") String apiKey,
                            @Value("${openai.embedding-model:text-embedding-3-small}") String modelName) {
        openAiModel = apiKey.isBlank() ? null : OpenAiEmbeddingModel.builder()
                .apiKey(apiKey)
                .modelName(modelName)
                .build();
    }

    public double[] embed(String text) {
        if (openAiModel != null) {
            float[] values = openAiModel.embed(text).content().vector();
            double[] result = new double[values.length];
            for (int i = 0; i < values.length; i++) {
                result[i] = values[i];
            }
            return result;
        }

        return localEmbedding(text);
    }

    private double[] localEmbedding(String text) {
        double[] vector = new double[VECTOR_SIZE];
        Arrays.fill(vector, 0.0);
        String[] words = text.toLowerCase().split("\\W+");
        for (String word : words) {
            if (!word.isBlank()) {
                int index = Math.floorMod(word.hashCode(), VECTOR_SIZE);
                vector[index] += 1.0;
            }
        }
        double length = Arrays.stream(vector).map(value -> value * value).sum();
        length = Math.sqrt(length);
        if (length > 0) {
            for (int i = 0; i < vector.length; i++) {
                vector[i] /= length;
            }
        }
        return vector;
    }
}