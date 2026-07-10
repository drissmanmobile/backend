package com.drissman.adapters.inbound.rest;

import com.drissman.adapters.inbound.rest.dto.ApiResponse;
import com.drissman.service.MistralAiService;
import com.drissman.service.mistral.AiGeneratedExercise;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/quiz")
@RequiredArgsConstructor
public class AiQuizController {

    private final MistralAiService mistralAiService;

    @GetMapping("/generate")
    public Mono<ResponseEntity<ApiResponse<AiGeneratedExercise>>> generateQuiz(
            @RequestParam(defaultValue = "5") int questions) {
        
        return mistralAiService.generateTrafficCodeExercise(questions)
                .map(exercise -> ResponseEntity.ok(ApiResponse.ok(exercise, "Quiz généré avec succès")))
                .onErrorResume(e -> Mono.just(ResponseEntity.internalServerError()
                        .body(ApiResponse.error("Erreur lors de la génération du quiz: " + e.getMessage(), 500))));
    }
}
