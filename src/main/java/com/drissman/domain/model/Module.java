package com.drissman.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Module {

    private UUID id;
    private UUID schoolId;
    private String name;
    private ModuleCategory category;
    private String description;
    private Integer orderIndex;
    private Integer requiredHours;
    private LocalDateTime createdAt;

    public enum ModuleCategory {
        CODE,
        CONDUITE,
        EXAMEN_BLANC
    }
}
