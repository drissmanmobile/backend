package com.drissman.adapters.inbound.rest.mapper;

import com.drissman.adapters.inbound.rest.dto.EnrollmentViewDto;
import com.drissman.domain.model.Enrollment;
import com.drissman.domain.model.Offer;
import com.drissman.domain.model.School;
import com.drissman.domain.model.User;

public class EnrollmentRestMapper {

    private EnrollmentRestMapper() {
        // Utility class
    }

    public static EnrollmentViewDto toViewDto(Enrollment enrollment, Offer offer, User student, School school) {
        if (enrollment == null) {
            return null;
        }

        String studentFirstName = student != null && student.getFirstName() != null ? student.getFirstName() : "";
        String studentLastName = student != null && student.getLastName() != null ? student.getLastName() : "";
        String studentName = (studentFirstName + " " + studentLastName).trim();
        if (studentName.isEmpty()) {
            studentName = "Eleve Inconnu";
        }

        String schoolName = school != null && school.getName() != null ? school.getName() : "Auto-ecole";
        String offerName = offer != null && offer.getName() != null ? offer.getName() : "Offre";
        Integer offerPrice = offer != null && offer.getPrice() != null ? offer.getPrice() : 0;
        String permitType = offer != null && offer.getPermitType() != null ? offer.getPermitType() : "B";

        return EnrollmentViewDto.builder()
                .id(enrollment.getId())
                .offerId(enrollment.getOfferId())
                .offerName(offerName)
                .price(offerPrice)
                .hours(enrollment.getHoursPurchased())
                .hoursConsumed(enrollment.getHoursConsumed() != null ? enrollment.getHoursConsumed() : 0)
                .hoursRemaining(enrollment.getRemainingHours() != null ? enrollment.getRemainingHours() : 0)
                .permitType(permitType)
                .schoolId(enrollment.getSchoolId())
                .schoolName(schoolName)
                .studentId(enrollment.getUserId())
                .studentName(studentName)
                .status(enrollment.getStatus().name())
                .enrolledAt(enrollment.getEnrolledAt())
                .build();
    }
}
