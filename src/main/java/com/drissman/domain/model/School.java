package com.drissman.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class School {
    private UUID id;
    private String name;
    private String description;
    private String address;
    private String city;
    private String region;
    private String phone;
    private String email;
    private String website;
    private BigDecimal rating;
    private String imageUrl;
    private Double latitude;
    private Double longitude;
    
    @Builder.Default
    private Boolean isVerified = false;
    
    @Builder.Default
    private Boolean isDemo = false;
    
    @Builder.Default
    private Boolean isActive = true;
    
    private LocalDateTime createdAt;
    
    private List<Offer> offers;
}
