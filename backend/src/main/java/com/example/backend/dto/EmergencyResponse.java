package com.example.backend.dto;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class EmergencyResponse {
    private TriageGuidance guidance;
    private List<NearbyPlaceResponse> nearestHospitals;
}