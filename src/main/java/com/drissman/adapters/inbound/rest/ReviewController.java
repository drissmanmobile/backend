package com.drissman.adapters.inbound.rest;

import com.drissman.adapters.inbound.rest.dto.ApiResponse;
import com.drissman.adapters.inbound.rest.dto.CreateReviewRequest;
import com.drissman.adapters.inbound.rest.dto.ReviewDto;
import com.drissman.adapters.inbound.rest.mapper.ReviewRestMapper;
import com.drissman.ports.inbound.ReviewUseCase;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.security.Principal;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/reviews")
@RequiredArgsConstructor
@Slf4j
public class ReviewController {

    private final ReviewUseCase reviewUseCase;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<ApiResponse<ReviewDto>> create(
            Principal principal,
            @Valid @RequestBody CreateReviewRequest request) {
        if (principal == null) {
            return Mono.error(new RuntimeException("Authentification requise pour laisser un avis"));
        }
        UUID userId = UUID.fromString(principal.getName());
        return reviewUseCase.create(userId, request.getSchoolId(), request.getRating(), request.getComment())
                .map(ReviewRestMapper::toDto)
                .map(data -> ApiResponse.created(data, "Avis publié avec succès"));
    }

    @GetMapping("/school/{schoolId}")
    public Mono<ApiResponse<List<ReviewDto>>> getBySchool(@PathVariable UUID schoolId) {
        return reviewUseCase.findBySchoolId(schoolId)
                .map(ReviewRestMapper::toDto)
                .collectList()
                .map(ApiResponse::ok);
    }
}
