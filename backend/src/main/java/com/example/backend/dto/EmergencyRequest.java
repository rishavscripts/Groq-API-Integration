package com.example.backend.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class EmergencyRequest {
    @NotBlank(message = "Medical condition description cannot be empty")
    private String condition;

    @NotNull(message = "Latitude is required")
    private Double latitude;

    @NotNull(message = "Longitude is required")
    private Double longitude;

    private int radiusMeters = 5000; // Default 5 km search radius
}
