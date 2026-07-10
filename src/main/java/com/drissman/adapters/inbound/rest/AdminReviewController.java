package com.drissman.adapters.inbound.rest;

import com.drissman.adapters.inbound.rest.dto.ReviewDto;
import com.drissman.adapters.inbound.rest.mapper.ReviewRestMapper;
import com.drissman.ports.inbound.ReviewUseCase;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.util.UUID;

@RestController
@RequestMapping("/api/schools/admin/reviews")
@RequiredArgsConstructor
@Slf4j
public class AdminReviewController {

    private final ReviewUseCase reviewUseCase;

    /**
     * Verify a review (admin only)
     */
    @PatchMapping("/{id}/verify")
    public Mono<ReviewDto> verify(@PathVariable UUID id) {
        return reviewUseCase.verifyReview(id)
                .map(ReviewRestMapper::toDto);
    }

    /**
     * Delete a review
     */
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public Mono<Void> delete(@PathVariable UUID id) {
        return reviewUseCase.delete(id);
    }
}
