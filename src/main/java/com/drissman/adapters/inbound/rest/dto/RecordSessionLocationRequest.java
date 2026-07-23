package com.drissman.adapters.inbound.rest.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RecordSessionLocationRequest {
    @NotNull(message = "Latitude requise")
    private Double latitude;

    @NotNull(message = "Longitude requise")
    private Double longitude;

    private Double speed;
    private Double heading;
}
