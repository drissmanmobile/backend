package com.drissman.adapters.outbound.persistence.mapper;

import com.drissman.adapters.outbound.persistence.entity.DocumentEntity;
import com.drissman.domain.model.Document;
import org.springframework.stereotype.Component;

@Component
public class DocumentPersistenceMapper {

    public Document toDomain(DocumentEntity entity) {
        if (entity == null) {
            return null;
        }

        return Document.builder()
                .id(entity.getId())
                .name(entity.getName())
                .fileUrl(entity.getFileUrl())
                .uploaderId(entity.getUploaderId())
                .moduleId(entity.getModuleId())
                .sessionId(entity.getSessionId())
                .schoolId(entity.getSchoolId())
                .offerId(entity.getOfferId())
                .createdAt(entity.getCreatedAt())
                .build();
    }

    public DocumentEntity toEntity(Document domain) {
        if (domain == null) {
            return null;
        }

        return DocumentEntity.builder()
                .id(domain.getId())
                .name(domain.getName())
                .fileUrl(domain.getFileUrl())
                .uploaderId(domain.getUploaderId())
                .moduleId(domain.getModuleId())
                .sessionId(domain.getSessionId())
                .schoolId(domain.getSchoolId())
                .offerId(domain.getOfferId())
                .createdAt(domain.getCreatedAt())
                .build();
    }
}
