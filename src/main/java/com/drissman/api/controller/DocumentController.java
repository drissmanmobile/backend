package com.drissman.api.controller;

import com.drissman.api.dto.DocumentDto;
import com.drissman.domain.repository.UserRepository;
import com.drissman.service.DocumentService;
import com.drissman.service.ImageStorageService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.http.codec.multipart.FormFieldPart;
import org.springframework.http.codec.multipart.Part;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.security.Principal;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/documents")
@RequiredArgsConstructor
public class DocumentController {

    private final DocumentService documentService;
    private final ImageStorageService storageService; // Reusing ImageStorageService to store files on disk
    private final UserRepository userRepository;

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public Mono<DocumentDto> uploadDocument(
            Principal principal,
            org.springframework.web.server.ServerWebExchange exchange) {

        if (principal == null) {
            return Mono.error(new ResponseStatusException(HttpStatus.UNAUTHORIZED));
        }
        UUID uploaderId = UUID.fromString(principal.getName());

        return userRepository.findById(uploaderId)
                .flatMap(user -> {
                    if (user.getSchoolId() == null) {
                        return Mono.error(new ResponseStatusException(HttpStatus.FORBIDDEN, "No school associated"));
                    }
                    
                    return exchange.getMultipartData().flatMap(multipartData -> {
                        Part part = multipartData.getFirst("file");
                        if (!(part instanceof FilePart)) {
                            return Mono.error(new ResponseStatusException(HttpStatus.BAD_REQUEST, "File is missing or invalid"));
                        }
                        FilePart filePart = (FilePart) part;
                        
                        Part categoryPart = multipartData.getFirst("category");
                        String category = "Administratif";
                        if (categoryPart instanceof FormFieldPart) {
                            category = ((FormFieldPart) categoryPart).value();
                        }
                        
                        Part enrollmentIdPart = multipartData.getFirst("enrollmentId");
                        UUID enrollmentId = null;
                        if (enrollmentIdPart instanceof FormFieldPart) {
                            String value = ((FormFieldPart) enrollmentIdPart).value();
                            if (value != null && !value.isEmpty()) {
                                enrollmentId = UUID.fromString(value);
                            }
                        }
                        
                        UUID finalEnrollmentId = enrollmentId;
                        String finalCategory = category;
                        
                        return org.springframework.core.io.buffer.DataBufferUtils
                                .join(filePart.content())
                                .flatMap(buffer -> {
                                    byte[] bytes = new byte[buffer.readableByteCount()];
                                    buffer.read(bytes);
                                    org.springframework.core.io.buffer.DataBufferUtils.release(buffer);
                                    long sizeBytes = bytes.length;
                                    
                                    return storageService.saveBytes(bytes, filePart.filename())
                                            .flatMap(filename -> {
                                                String fileUrl = "/api/images/" + filename; // Use image controller to serve the file
                                                return documentService.saveDocument(
                                                        uploaderId, 
                                                        user.getSchoolId(), 
                                                        filePart.filename(), 
                                                        fileUrl, 
                                                        filePart.headers().getContentType() != null ? filePart.headers().getContentType().toString() : "application/pdf", 
                                                        sizeBytes, 
                                                        finalEnrollmentId, 
                                                        finalCategory
                                                );
                                            });
                                });
                    });
                });
    }

    @GetMapping("/school")
    public Flux<DocumentDto> getSchoolDocuments(Principal principal) {
        if (principal == null) return Flux.empty();
        UUID userId = UUID.fromString(principal.getName());
        return userRepository.findById(userId)
                .flatMapMany(user -> {
                    if (user.getSchoolId() == null) return Flux.empty();
                    return documentService.getSchoolDocuments(user.getSchoolId());
                });
    }

    @GetMapping("/me")
    public Flux<DocumentDto> getMyDocuments(Principal principal) {
        if (principal == null) return Flux.empty();
        UUID userId = UUID.fromString(principal.getName());
        return documentService.getDocumentsForUser(userId);
    }
}
