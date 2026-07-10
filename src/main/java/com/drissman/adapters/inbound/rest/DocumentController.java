package com.drissman.adapters.inbound.rest;

import com.drissman.domain.model.Document;
import com.drissman.service.DocumentApplicationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/api/v1/documents")
@RequiredArgsConstructor
public class DocumentController {

    private final DocumentApplicationService documentService;

    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<Document> uploadDocument(
            @RequestPart("file") FilePart filePart,
            @RequestPart("uploaderId") String uploaderIdStr,
            @RequestPart(value = "moduleId", required = false) String moduleIdStr,
            @RequestPart(value = "sessionId", required = false) String sessionIdStr,
            @RequestPart(value = "schoolId", required = false) String schoolIdStr,
            @RequestPart(value = "offerId", required = false) String offerIdStr) {
        
        UUID uploaderId = UUID.fromString(uploaderIdStr);
        UUID moduleId = moduleIdStr != null && !moduleIdStr.isEmpty() ? UUID.fromString(moduleIdStr) : null;
        UUID sessionId = sessionIdStr != null && !sessionIdStr.isEmpty() ? UUID.fromString(sessionIdStr) : null;
        UUID schoolId = schoolIdStr != null && !schoolIdStr.isEmpty() ? UUID.fromString(schoolIdStr) : null;
        UUID offerId = offerIdStr != null && !offerIdStr.isEmpty() ? UUID.fromString(offerIdStr) : null;

        return documentService.uploadDocument(filePart, uploaderId, moduleId, sessionId, schoolId, offerId);
    }

    @GetMapping("/module/{moduleId}")
    public Flux<Document> getDocumentsByModule(@PathVariable UUID moduleId) {
        return documentService.getDocumentsByModule(moduleId);
    }

    @GetMapping("/session/{sessionId}")
    public Flux<Document> getDocumentsBySession(@PathVariable UUID sessionId) {
        return documentService.getDocumentsBySession(sessionId);
    }

    @GetMapping("/school/{schoolId}")
    public Flux<Document> getDocumentsBySchool(@PathVariable UUID schoolId) {
        return documentService.getDocumentsBySchool(schoolId);
    }

    @GetMapping("/offer/{offerId}")
    public Flux<Document> getDocumentsByOffer(@PathVariable UUID offerId) {
        return documentService.getDocumentsByOffer(offerId);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public Mono<Void> deleteDocument(@PathVariable UUID id) {
        return documentService.deleteDocument(id);
    }
}
