package com.drissman.adapters.inbound.rest.dto;

import com.drissman.domain.model.Session;
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
public class MonitorSessionViewDto {
    private UUID sessionId;
    private UUID enrollmentId;
    private UUID studentId;
    private String studentName;
    private UUID offerId;
    private String offerName;
    private LocalDate date;
    private LocalTime startTime;
    private LocalTime endTime;
    private String meetingPoint;
    private String pedagogicalNotes;
    private Session.SessionStatus status;
    private int durationHours;
}
