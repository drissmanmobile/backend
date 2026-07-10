package com.drissman.service;

import com.drissman.service.mistral.AiGeneratedExercise;
import com.drissman.service.mistral.MistralRequest;
import com.drissman.service.mistral.MistralResponse;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.List;

@Service
public class MistralAiService {

    private final WebClient webClient;
    private final ObjectMapper objectMapper;
    private final String model;

    public MistralAiService(
            WebClient.Builder webClientBuilder,
            ObjectMapper objectMapper,
            @Value("${mistral.api.url:https://api.mistral.ai/v1/chat/completions}") String apiUrl,
            @Value("${mistral.api.key:}") String apiKey) {
        
        this.objectMapper = objectMapper;
        // You can use mistral-tiny, mistral-small, or open-mistral-nemo
        this.model = "mistral-small-latest";
        
        this.webClient = webClientBuilder
                .baseUrl(apiUrl)
                .defaultHeader(HttpHeaders.AUTHORIZATION, "Bearer " + apiKey)
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .build();
    }

    public Mono<AiGeneratedExercise> generateTrafficCodeExercise(int numberOfQuestions) {
        String prompt = "Tu es un expert du code de la route au Cameroun. " +
                "Génère un test de " + numberOfQuestions + " questions à choix multiples. " +
                "Réponds UNIQUEMENT avec un objet JSON valide suivant exactement cette structure : " +
                "{\"title\": \"Entraînement Code Cameroun\", \"questions\": [ " +
                "{\"content\": \"Question texte\", \"explanation\": \"Explication détaillée de la réponse\", \"choices\": [ " +
                "{\"content\": \"Choix A\", \"isCorrect\": true}, {\"content\": \"Choix B\", \"isCorrect\": false} ]} ]}. " +
                "Assure-toi qu'il y ait exactement 4 choix par question et qu'un seul soit vrai.";

        MistralRequest request = MistralRequest.builder()
                .model(model)
                .response_format(MistralRequest.ResponseFormat.builder().type("json_object").build())
                .messages(List.of(
                        MistralRequest.Message.builder()
                                .role("user")
                                .content(prompt)
                                .build()
                ))
                .build();

        return webClient.post()
                .bodyValue(request)
                .retrieve()
                .bodyToMono(MistralResponse.class)
                .map(response -> {
                    String jsonContent = response.getChoices().get(0).getMessage().getContent();
                    try {
                        return objectMapper.readValue(jsonContent, AiGeneratedExercise.class);
                    } catch (JsonProcessingException e) {
                        throw new RuntimeException("Failed to parse Mistral AI JSON response", e);
                    }
                });
    }
}
