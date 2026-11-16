package com.example.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TriageGuidance {
    private String urgencyLevel;          // HIGH, MODERATE, LOW
    private String recommendedSpecialty;  // e.g., Cardiology, Emergency Medicine
    private List<String> immediateActions;
    private String disclaimer;
}
