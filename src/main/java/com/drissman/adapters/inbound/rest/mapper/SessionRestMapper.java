package com.drissman.adapters.inbound.rest.mapper;

import com.drissman.adapters.inbound.rest.dto.*;
import com.drissman.domain.model.User;
import com.drissman.domain.model.Enrollment;
import com.drissman.domain.model.Offer;
import com.drissman.domain.model.Session;

import java.util.Locale;
import java.util.UUID;

public class SessionRestMapper {

    private SessionRestMapper() {
        // Utility class
    }

    public static SessionDto toDto(Session session) {
        if (session == null) {
            return null;
        }
        return SessionDto.builder()
                .id(session.getId())
                .enrollmentId(session.getEnrollmentId())
                .offerId(session.getOfferId())
                .monitorId(session.getMonitorId())
                .vehicleId(session.getVehicleId())
                .moduleId(session.getModuleId())
                .lessonId(session.getLessonId())
                .date(session.getDate())
                .startTime(session.getStartTime())
                .endTime(session.getEndTime())
                .status(session.getStatus())
                .meetingPoint(session.getMeetingPoint())
                .pedagogicalNotes(session.getPedagogicalNotes())
                .durationHours(session.getDurationHours())
                .build();
    }

    public static AvailableOfferDto toAvailableOfferDto(Offer offer) {
        if (offer == null) {
            return null;
        }
        return AvailableOfferDto.builder()
                .offerId(offer.getId())
                .offerName(offer.getName())
                .permitType(offer.getPermitType() != null ? offer.getPermitType().toUpperCase(Locale.ROOT) : "B")
                .price(offer.getPrice())
                .build();
    }

    public static SessionEnrollmentOptionDto toEnrollmentOptionDto(Enrollment enrollment, User user) {
        if (enrollment == null) {
            return null;
        }
        String studentFirstName = user != null && user.getFirstName() != null ? user.getFirstName() : "";
        String studentLastName = user != null && user.getLastName() != null ? user.getLastName() : "";
        String studentName = (studentFirstName + " " + studentLastName).trim();
        if (studentName.isEmpty()) {
            studentName = "Eleve Inconnu";
        }

        return SessionEnrollmentOptionDto.builder()
                .enrollmentId(enrollment.getId())
                .studentId(enrollment.getUserId())
                .studentName(studentName)
                .status(enrollment.getStatus().name())
                .hoursPurchased(enrollment.getHoursPurchased())
                .hoursConsumed(enrollment.getHoursConsumed())
                .build();
    }

    public static MonitorSessionViewDto toMonitorSessionView(Session session, Enrollment enrollment, User student, Offer offer) {
        if (session == null) {
            return null;
        }
        String studentFirstName = student != null && student.getFirstName() != null ? student.getFirstName() : "";
        String studentLastName = student != null && student.getLastName() != null ? student.getLastName() : "";
        String studentName = (studentFirstName + " " + studentLastName).trim();
        if (studentName.isEmpty()) {
            studentName = "Eleve Inconnu";
        }

        String offerName = offer != null && offer.getName() != null ? offer.getName() : "Offre";
        UUID offerId = offer != null ? offer.getId() : (enrollment != null ? enrollment.getOfferId() : null);
        UUID studentId = student != null ? student.getId() : (enrollment != null ? enrollment.getUserId() : null);

        return MonitorSessionViewDto.builder()
                .sessionId(session.getId())
                .enrollmentId(session.getEnrollmentId())
                .studentId(studentId)
                .studentName(studentName)
                .offerId(offerId)
                .offerName(offerName)
                .date(session.getDate())
                .startTime(session.getStartTime())
                .endTime(session.getEndTime())
                .meetingPoint(session.getMeetingPoint())
                .pedagogicalNotes(session.getPedagogicalNotes())
                .status(session.getStatus())
                .durationHours(session.getDurationHours())
                .build();
    }

    public static MonitorStudentProgressDto toMonitorStudentProgress(Enrollment enrollment, User student, Offer offer) {
        if (enrollment == null) {
            return null;
        }
        String studentFirstName = student != null && student.getFirstName() != null ? student.getFirstName() : "";
        String studentLastName = student != null && student.getLastName() != null ? student.getLastName() : "";
        String studentName = (studentFirstName + " " + studentLastName).trim();
        if (studentName.isEmpty()) {
            studentName = "Eleve Inconnu";
        }

        String offerName = offer != null && offer.getName() != null ? offer.getName() : "Offre";
        UUID offerId = offer != null ? offer.getId() : enrollment.getOfferId();

        Integer consumed = enrollment.getHoursConsumed() != null ? enrollment.getHoursConsumed() : 0;
        Integer purchased = enrollment.getHoursPurchased() != null ? enrollment.getHoursPurchased() : 0;
        Integer remaining = Math.max(0, purchased - consumed);

        return MonitorStudentProgressDto.builder()
                .enrollmentId(enrollment.getId())
                .studentId(enrollment.getUserId())
                .studentName(studentName)
                .offerId(offerId)
                .offerName(offerName)
                .hoursPurchased(purchased)
                .hoursConsumed(consumed)
                .hoursRemaining(remaining)
                .status(enrollment.getStatus().name())
                .build();
    }

    public static CandidateSessionViewDto toCandidateSessionView(Session session, Enrollment enrollment, Offer offer, String monitorName) {
        if (session == null) {
            return null;
        }

        String offerName = offer != null && offer.getName() != null ? offer.getName() : "Offre";
        UUID offerId = offer != null ? offer.getId() : (enrollment != null ? enrollment.getOfferId() : null);

        return CandidateSessionViewDto.builder()
                .sessionId(session.getId())
                .enrollmentId(session.getEnrollmentId())
                .offerId(offerId)
                .offerName(offerName)
                .monitorName(monitorName != null ? monitorName : "Non assigne")
                .date(session.getDate())
                .startTime(session.getStartTime())
                .endTime(session.getEndTime())
                .meetingPoint(session.getMeetingPoint())
                .status(session.getStatus())
                .durationHours(session.getDurationHours())
                .build();
    }
}
