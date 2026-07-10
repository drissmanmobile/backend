package com.drissman.service;

import com.drissman.domain.model.School;
import com.drissman.ports.inbound.SchoolUseCase;
import com.drissman.ports.outbound.OfferRepositoryPort;
import com.drissman.ports.outbound.SchoolRepositoryPort;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Comparator;
import java.util.UUID;

@RequiredArgsConstructor
public class SchoolApplicationService implements SchoolUseCase {

    private final SchoolRepositoryPort schoolRepositoryPort;
    private final OfferRepositoryPort offerRepositoryPort;

    /** Earth radius in km for Haversine formula */
    private static final double EARTH_RADIUS_KM = 6371.0;

    @Override
    public Flux<School> findAll(String city) {
        Flux<School> schools = ((city != null && !city.isBlank())
                ? schoolRepositoryPort.findByCityOrderByRatingDesc(city)
                : schoolRepositoryPort.findAll())
                .filter(school -> Boolean.TRUE.equals(school.getIsActive()));

        return schools.flatMap(school -> 
            offerRepositoryPort.findBySchoolId(school.getId())
                .collectList()
                .flatMap(offers -> {
                    // Les écoles sont visibles si elles sont actives (géré via findAll/findByCity). 
                    // Nous n'exigeons plus qu'elles aient des offres par défaut.
                    school.setOffers(offers);
                    return Mono.just(school);
                })
        );
    }

    @Override
    public Flux<School> findNearby(double lat, double lng, double radiusKm) {
        return schoolRepositoryPort.findAll()
                .filter(school -> Boolean.TRUE.equals(school.getIsActive()))
                .filter(school -> school.getLatitude() != null && school.getLongitude() != null)
                .filter(school -> haversineDistance(lat, lng, school.getLatitude(), school.getLongitude()) <= radiusKm)
                .collectSortedList(Comparator.comparingDouble(
                        school -> haversineDistance(lat, lng, school.getLatitude(), school.getLongitude())))
                .flatMapMany(Flux::fromIterable)
                .flatMap(school -> 
                    offerRepositoryPort.findBySchoolId(school.getId())
                        .collectList()
                        .map(offers -> {
                            school.setOffers(offers);
                            return school;
                        })
                );
    }

    @Override
    public Mono<School> findById(UUID id) {
        if (id == null) {
            return Mono.empty();
        }
        return schoolRepositoryPort.findById(id)
                .flatMap(school -> 
                    offerRepositoryPort.findBySchoolId(school.getId())
                        .collectList()
                        .map(offers -> {
                            school.setOffers(offers);
                            return school;
                        })
                );
    }

    @Override
    public Mono<School> update(UUID id, String name, String description, String address, String city, String region, String phone, String email, String website, String imageUrl) {
        return schoolRepositoryPort.findById(id)
                .flatMap(school -> {
                    if (name != null) school.setName(name);
                    if (description != null) school.setDescription(description);
                    if (address != null) school.setAddress(address);
                    if (city != null) school.setCity(city);
                    if (region != null) school.setRegion(region);
                    if (phone != null) school.setPhone(phone);
                    if (email != null) school.setEmail(email);
                    if (website != null) school.setWebsite(website);
                    if (imageUrl != null) school.setImageUrl(imageUrl);
                    return schoolRepositoryPort.save(school);
                })
                .flatMap(savedSchool -> savedSchool != null ? findById(savedSchool.getId()) : Mono.empty());
    }

    @Override
    public Mono<School> save(School school) {
        return schoolRepositoryPort.save(school);
    }

    /**
     * Calculate distance between two GPS points using the Haversine formula.
     * 
     * @return distance in kilometers
     */
    static double haversineDistance(double lat1, double lon1, double lat2, double lon2) {
        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                        * Math.sin(dLon / 2) * Math.sin(dLon / 2);
        double c = 2 * Math.asin(Math.sqrt(a));
        return EARTH_RADIUS_KM * c;
    }
}
