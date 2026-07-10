package com.drissman.service;

import com.drissman.adapters.inbound.rest.dto.PartnerStatsDto;
import com.drissman.domain.model.Enrollment;
import com.drissman.domain.model.Session;
import com.drissman.ports.outbound.UserRepositoryPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;
import java.util.List;
import java.util.Comparator;

import com.drissman.adapters.inbound.rest.dto.AdminDashboardDto;
import com.drissman.adapters.inbound.rest.dto.RecentActivityDto;
import com.drissman.adapters.inbound.rest.dto.UpcomingSessionDto;
import com.drissman.ports.outbound.ModuleRepositoryPort;
import com.drissman.ports.outbound.EnrollmentRepositoryPort;
import com.drissman.ports.outbound.OfferRepositoryPort;
import com.drissman.ports.outbound.SessionRepositoryPort;
import com.drissman.ports.outbound.MonitorRepositoryPort;

@Service
@RequiredArgsConstructor
public class AdminSchoolService {

        private final EnrollmentRepositoryPort enrollmentRepository;
        private final OfferRepositoryPort offerRepository;
        private final UserRepositoryPort userRepository;
        private final SessionRepositoryPort sessionRepository;
        private final MonitorRepositoryPort monitorRepository;
        private final ModuleRepositoryPort moduleRepository;

        public Mono<PartnerStatsDto> getStats(UUID schoolId) {
                if (schoolId == null) {
                        return Mono.empty();
                }

                Mono<Long> enrollmentCount = enrollmentRepository.findBySchoolId(schoolId).count();

                Mono<Long> upcomingSessions = sessionRepository.findBySchoolId(schoolId)
                                .filter(s -> s.getDate() != null && !s.getDate().isBefore(LocalDate.now()))
                                .filter(s -> s.getStatus() == Session.SessionStatus.SCHEDULED
                                                || s.getStatus() == Session.SessionStatus.CONFIRMED)
                                .count();

                Mono<Long> totalRevenue = enrollmentRepository.findBySchoolId(schoolId)
                                .filter(e -> e.getStatus() == Enrollment.EnrollmentStatus.ACTIVE
                                                || e.getStatus() == Enrollment.EnrollmentStatus.COMPLETED)
                                .flatMap(e -> offerRepository.findById(e.getOfferId()))
                                .map(offer -> offer.getPrice() != null ? offer.getPrice().longValue() : 0L)
                                .reduce(0L, Long::sum);

                return Mono.zip(enrollmentCount, upcomingSessions, totalRevenue)
                                .map(tuple -> PartnerStatsDto.builder()
                                                 .revenue(String.format("%,d FCFA", tuple.getT3()).replace(",", " "))
                                                 .enrollments(tuple.getT1().intValue())
                                                 .successRate("N/A")
                                                 .upcomingLessons(tuple.getT2().intValue())
                                                 .revenueGrowth(0.0)
                                                 .enrollmentGrowth(0)
                                                 .build());
        }

        public Mono<AdminDashboardDto> getDashboardStats(UUID schoolId) {
                if (schoolId == null) {
                        return Mono.empty();
                }

                Mono<Integer> activeCandidates = enrollmentRepository.findBySchoolId(schoolId)
                                .filter(e -> e.getStatus() == Enrollment.EnrollmentStatus.ACTIVE)
                                .count()
                                .map(Long::intValue);

                Mono<Integer> totalOffers = offerRepository.findBySchoolId(schoolId)
                                .count()
                                .map(Long::intValue);

                Mono<Integer> totalModules = moduleRepository.findBySchoolIdOrderByOrderIndexAsc(schoolId)
                                .count()
                                .map(Long::intValue);

                Mono<Integer> todaySessionsCount = sessionRepository.findBySchoolId(schoolId)
                                .filter(s -> s.getDate() != null && s.getDate().equals(LocalDate.now()))
                                .filter(s -> s.getStatus() != Session.SessionStatus.CANCELLED)
                                .count()
                                .map(Long::intValue);

                Mono<List<RecentActivityDto>> recentActivities = enrollmentRepository.findBySchoolId(schoolId)
                                .sort(Comparator.comparing((Enrollment e) -> e.getCreatedAt() != null ? e.getCreatedAt()
                                                : LocalDateTime.MIN).reversed())
                                .take(5)
                                .flatMap(e -> userRepository.findById(e.getUserId())
                                                .map(user -> RecentActivityDto.builder()
                                                                .id(e.getId().toString())
                                                                .title("Nouvelle inscription")
                                                                .description(user.getFirstName() + " "
                                                                                + user.getLastName() + " s'est inscrit")
                                                                .type("ENROLLMENT")
                                                                .timestamp(e.getCreatedAt() != null ? e.getCreatedAt()
                                                                                : LocalDateTime.now())
                                                                .build()))
                                .collectList();

                Mono<List<UpcomingSessionDto>> upcomingSessions = sessionRepository.findBySchoolId(schoolId)
                                .filter(s -> s.getDate() != null && !s.getDate().isBefore(LocalDate.now()))
                                .filter(s -> s.getStatus() == Session.SessionStatus.SCHEDULED
                                                || s.getStatus() == Session.SessionStatus.CONFIRMED)
                                .sort(Comparator.comparing(Session::getDate)
                                                .thenComparing(s -> s.getStartTime() != null ? s.getStartTime()
                                                                : java.time.LocalTime.MIN))
                                .take(5)
                                .flatMap(s -> {
                                        Mono<String> studentNameMono = enrollmentRepository
                                                        .findById(s.getEnrollmentId())
                                                        .flatMap(e -> userRepository.findById(e.getUserId()))
                                                        .map(u -> u.getFirstName() + " " + u.getLastName())
                                                        .defaultIfEmpty("Élève inconnu");

                                        Mono<String> monitorNameMono = s.getMonitorId() != null
                                                        ? monitorRepository.findById(s.getMonitorId())
                                                                        .flatMap(monitor -> monitor.getUserId() != null
                                                                                        ? userRepository.findById(
                                                                                                        monitor.getUserId())
                                                                                                        .map(u -> u.getFirstName()
                                                                                                                        + " "
                                                                                                                        + u.getLastName())
                                                                                        : Mono.just(
                                                                                                        monitor.getFirstName()
                                                                                                                        + " "
                                                                                                                        + monitor.getLastName()))
                                                                        .defaultIfEmpty("Non assigné")
                                                        : Mono.just("Non assigné");

                                        return Mono.zip(studentNameMono, monitorNameMono)
                                                        .map(names -> UpcomingSessionDto.builder()
                                                                        .id(s.getId().toString())
                                                                        .monitorName(names.getT2())
                                                                        .studentName(names.getT1())
                                                                        .date(s.getDate())
                                                                        .startTime(s.getStartTime() != null
                                                                                        ? s.getStartTime().toString()
                                                                                        : "")
                                                                        .endTime(s.getEndTime() != null
                                                                                        ? s.getEndTime().toString()
                                                                                        : "")
                                                                        .meetingPoint(s.getMeetingPoint())
                                                                        .status(s.getStatus() != null
                                                                                        ? s.getStatus().name()
                                                                                        : "SCHEDULED")
                                                                        .build());
                                })
                                .collectList();

                Mono<Long> totalRevenue = enrollmentRepository.findBySchoolId(schoolId)
                                .filter(e -> e.getStatus() == Enrollment.EnrollmentStatus.ACTIVE
                                                || e.getStatus() == Enrollment.EnrollmentStatus.COMPLETED)
                                .flatMap(e -> offerRepository.findById(e.getOfferId()))
                                .map(offer -> offer.getPrice() != null ? offer.getPrice().longValue() : 0L)
                                .reduce(0L, Long::sum);

                Mono<Long> monthlyRevenue = enrollmentRepository.findBySchoolId(schoolId)
                                .filter(e -> e.getStatus() == Enrollment.EnrollmentStatus.ACTIVE
                                                || e.getStatus() == Enrollment.EnrollmentStatus.COMPLETED)
                                .filter(e -> e.getCreatedAt() != null
                                                && e.getCreatedAt().getMonth() == LocalDate.now().getMonth()
                                                && e.getCreatedAt().getYear() == LocalDate.now().getYear())
                                .flatMap(e -> offerRepository.findById(e.getOfferId()))
                                .map(offer -> offer.getPrice() != null ? offer.getPrice().longValue() : 0L)
                                .reduce(0L, Long::sum);

                Mono<Integer> pendingValidations = enrollmentRepository.findBySchoolId(schoolId)
                                .filter(e -> e.getStatus() == Enrollment.EnrollmentStatus.PENDING)
                                .count()
                                .map(Long::intValue);

                var metricsZip = Mono.zip(activeCandidates, totalOffers, totalModules);
                var revenueZip = Mono.zip(todaySessionsCount, totalRevenue, monthlyRevenue);
                var complexZip = Mono.zip(pendingValidations, recentActivities, upcomingSessions);

                return Mono.zip(metricsZip, revenueZip, complexZip)
                                .map(tuple -> AdminDashboardDto.builder()
                                                .activeCandidates(tuple.getT1().getT1())
                                                .totalOffers(tuple.getT1().getT2())
                                                .totalModules(tuple.getT1().getT3())
                                                .todaySessions(tuple.getT2().getT1())
                                                .totalRevenue(tuple.getT2().getT2())
                                                .monthlyRevenue(tuple.getT2().getT3())
                                                .pendingValidations(tuple.getT3().getT1())
                                                .recentActivities(tuple.getT3().getT2())
                                                .upcomingSessions(tuple.getT3().getT3())
                                                .build());
        }
}
