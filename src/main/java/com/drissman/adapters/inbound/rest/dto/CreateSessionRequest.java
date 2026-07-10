package com.drissman.adapters.inbound.rest.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateSessionRequest {

    @NotNull(message = "L'ID de l'inscription est obligatoire")
    private UUID enrollmentId;

    @NotNull(message = "L'ID de l'offre est obligatoire")
    private UUID offerId;

    private UUID monitorId;

    @NotNull(message = "La date est obligatoire")
    private LocalDate date;

    @NotNull(message = "L'heure de début est obligatoire")
    private LocalTime startTime;

    @NotNull(message = "L'heure de fin est obligatoire")
    private LocalTime endTime;

    private String meetingPoint;

    private UUID moduleId;

    private UUID lessonId;
}
