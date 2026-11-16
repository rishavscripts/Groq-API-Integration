package com.example.backend.controller;

import com.example.backend.dto.EmergencyRequest;
import com.example.backend.dto.EmergencyResponse;
import com.example.backend.dto.NearbyPlaceResponse;
import com.example.backend.dto.TriageGuidance;
import com.example.backend.service.GroqTriageService;
import com.example.backend.service.OverpassService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.concurrent.CompletableFuture;

@RestController
@RequestMapping("/api/emergency")
@CrossOrigin(origins = "http://localhost:5173")
@RequiredArgsConstructor
public class EmergencyController {
    private final GroqTriageService triageService;
    private final OverpassService overpassService;

    @PostMapping("/assist")
    public ResponseEntity<EmergencyResponse> getEmergencyAssistance(@Valid @RequestBody EmergencyRequest request) {

        CompletableFuture<TriageGuidance> guidanceFuture = CompletableFuture.supplyAsync(() ->
                triageService.generateImmediateActions(request.getCondition())
        );

        CompletableFuture<List<NearbyPlaceResponse>> hospitalsFuture = CompletableFuture.supplyAsync(() ->
                overpassService.findNearestHospitals(
                        request.getLatitude(),
                        request.getLongitude(),
                        request.getRadiusMeters()
                )
        );

        CompletableFuture.allOf(guidanceFuture, hospitalsFuture).join();

        EmergencyResponse response = EmergencyResponse.builder()
                .guidance(guidanceFuture.join())
                .nearestHospitals(hospitalsFuture.join().stream().limit(6).toList())
                .build();

        return ResponseEntity.ok(response);
    }
}
