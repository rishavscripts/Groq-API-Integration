package com.example.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NearbyPlaceResponse {
    private Long id;
    private String name;
    private String address;
    private String phone;
    private double latitude;
    private double longitude;
    private double distanceKm;
}
