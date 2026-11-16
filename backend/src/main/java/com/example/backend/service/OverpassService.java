package com.example.backend.service;

import com.example.backend.dto.NearbyPlaceResponse;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClient;
import tools.jackson.databind.JsonNode;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Service
public class OverpassService {
    private final RestClient restClient;

    public OverpassService() {
        this.restClient = RestClient.builder()
                .baseUrl("https://overpass-api.de/api")
                .build();
    }

    public List<NearbyPlaceResponse> findNearestHospitals(double userLat, double userLon, int radiusMeters) {
        List<NearbyPlaceResponse> hospitals = queryOverpass(userLat, userLon, radiusMeters);

        // Auto-expand radius to 10 km if fewer than 2 hospitals found nearby
        if (hospitals.size() < 2 && radiusMeters < 10000) {
            hospitals = queryOverpass(userLat, userLon, 10000);
        }

        return hospitals;
    }

    private List<NearbyPlaceResponse> queryOverpass(double userLat, double userLon, int radiusMeters) {
        String query = String.format("""
                [out:json][timeout:25];
                (
                  node["amenity"="hospital"](around:%d,%f,%f);
                  way["amenity"="hospital"](around:%d,%f,%f);
                );
                out center;
                """, radiusMeters, userLat, userLon, radiusMeters, userLat, userLon);

        MultiValueMap<String, String> formData = new LinkedMultiValueMap<>();
        formData.add("data", query);

        List<NearbyPlaceResponse> results = new ArrayList<>();

        try {
            JsonNode root = restClient.post()
                    .uri("/interpreter")
                    .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                    .body(formData)
                    .retrieve()
                    .body(JsonNode.class);

            if (root == null || !root.has("elements")) {
                return results;
            }

            for (JsonNode element : root.get("elements")) {
                JsonNode tags = element.get("tags");
                if (tags == null || !tags.has("name")) {
                    continue;
                }

                double placeLat = element.has("lat")
                        ? element.get("lat").asDouble()
                        : element.path("center").path("lat").asDouble();
                double placeLon = element.has("lon")
                        ? element.get("lon").asDouble()
                        : element.path("center").path("lon").asDouble();

                if (placeLat == 0.0 || placeLon == 0.0) continue;

                double distance = calculateHaversineKm(userLat, userLon, placeLat, placeLon);

                results.add(NearbyPlaceResponse.builder()
                        .id(element.get("id").asLong())
                        .name(tags.get("name").asText())
                        .address(buildAddress(tags))
                        .phone(tags.path("phone").asText(tags.path("contact:phone").asText("Not available")))
                        .latitude(placeLat)
                        .longitude(placeLon)
                        .distanceKm(Math.round(distance * 100.0) / 100.0)
                        .build());
            }

            results.sort(Comparator.comparingDouble(NearbyPlaceResponse::getDistanceKm));
        } catch (Exception e) {
            System.err.println("Overpass API error: " + e.getMessage());
        }

        return results;
    }

    private String buildAddress(JsonNode tags) {
        if (tags.has("addr:full")) {
            return tags.get("addr:full").asText();
        }
        String street = tags.path("addr:street").asText("");
        String city = tags.path("addr:city").asText("");
        String combined = (street + " " + city).trim();
        return combined.isEmpty() ? "Address details unavailable" : combined;
    }

    private double calculateHaversineKm(double lat1, double lon1, double lat2, double lon2) {
        final int EARTH_RADIUS_KM = 6371;
        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);

        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(dLon / 2) * Math.sin(dLon / 2);

        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        return EARTH_RADIUS_KM * c;
    }
}
