package com.drissman.service.mistral;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AiGeneratedExercise {
    private String title;
    private List<AiQuestion> questions;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class AiQuestion {
        private String content;
        private String explanation;
        private List<AiChoice> choices;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class AiChoice {
        private String content;
        private Boolean isCorrect;
    }
}
