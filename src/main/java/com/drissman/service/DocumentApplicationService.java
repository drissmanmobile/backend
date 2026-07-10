package com.drissman.service;

import com.drissman.domain.model.Document;
import com.drissman.ports.outbound.DocumentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.UUID;

import com.drissman.ports.outbound.EnrollmentRepositoryPort;

@Slf4j
@Service
@RequiredArgsConstructor
public class DocumentApplicationService {

    private final DocumentRepository documentRepository;
    private final ImageStorageService imageStorageService;
    private final EnrollmentRepositoryPort enrollmentRepository;
    private final FirebaseNotificationService notificationService;

    public Mono<Document> uploadDocument(FilePart filePart, UUID uploaderId, UUID moduleId, UUID sessionId, UUID schoolId, UUID offerId) {
        log.info("Uploading document for module {}, session {}, school {}, or offer {}", moduleId, sessionId, schoolId, offerId);
        return imageStorageService.save(filePart)
                .flatMap(filename -> {
                    String fileUrl = "/api/images/" + filename;
                    Document document = Document.builder()
                            .name(filePart.filename())
                            .fileUrl(fileUrl)
                            .uploaderId(uploaderId)
                            .moduleId(moduleId)
                            .sessionId(sessionId)
                            .schoolId(schoolId)
                            .offerId(offerId)
                            .createdAt(LocalDateTime.now())
                            .build();
                    return documentRepository.save(document);
                })
                .flatMap(savedDocument -> {
                    if (offerId != null) {
                        log.info("Sending notifications to students enrolled in offer {}", offerId);
                        return enrollmentRepository.findByOfferId(offerId)
                                .flatMap(enrollment -> 
                                    notificationService.sendNotificationToUser(
                                        enrollment.getUserId(),
                                        "Nouveau fichier ajouté",
                                        "Un nouveau fichier (" + savedDocument.getName() + ") a été ajouté à votre offre."
                                    )
                                )
                                .then(Mono.just(savedDocument));
                    }
                    return Mono.just(savedDocument);
                });
    }

    public Flux<Document> getDocumentsByModule(UUID moduleId) {
        return documentRepository.findByModuleId(moduleId);
    }

    public Flux<Document> getDocumentsBySession(UUID sessionId) {
        return documentRepository.findBySessionId(sessionId);
    }

    public Flux<Document> getDocumentsBySchool(UUID schoolId) {
        return documentRepository.findBySchoolId(schoolId);
    }

    public Flux<Document> getDocumentsByOffer(UUID offerId) {
        return documentRepository.findByOfferId(offerId);
    }

    public Mono<Void> deleteDocument(UUID id) {
        return documentRepository.findById(id)
                .flatMap(doc -> {
                    String filename = doc.getFileUrl().substring(doc.getFileUrl().lastIndexOf('/') + 1);
                    return imageStorageService.delete(filename)
                            .then(documentRepository.deleteById(id));
                })
                .switchIfEmpty(documentRepository.deleteById(id));
    }
}
