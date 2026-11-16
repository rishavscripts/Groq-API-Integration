package com.example.backend.service;

import com.example.backend.dto.TriageGuidance;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

import java.util.List;
import java.util.Map;

@Service
public class GroqTriageService {
    private final RestClient restClient;
    private final ObjectMapper objectMapper;

    public GroqTriageService(@Value("${groq.api.key}") String apiKey, ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
        this.restClient = RestClient.builder()
                .baseUrl("https://api.groq.com/openai/v1")
                .defaultHeader("Authorization", "Bearer " + apiKey)
                .defaultHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
                .build();
    }

    public TriageGuidance generateImmediateActions(String userCondition) {
        String systemPrompt = """
                You are an emergency first-aid triage assistant. Based on the user's described medical condition, respond ONLY with a valid JSON object matching this exact structure:
                {
                  "urgencyLevel": "HIGH" | "MODERATE" | "LOW",
                  "recommendedSpecialty": "relevant hospital department",
                  "immediateActions": ["step 1", "step 2", "step 3", "step 4"],
                  "disclaimer": "Brief medical disclaimer advising to call local emergency services (102/108/112) immediately if life-threatening."
                }
                Keep immediateActions concise, calm, actionable, and safe for a layperson. Do not prescribe medications.
                """;

        Map<String, Object> requestBody = Map.of(
                "model", "openai/gpt-oss-120b",
                "response_format", Map.of("type", "json_object"),
                "temperature", 0.2,
                "messages", List.of(
                        Map.of("role", "system", "content", systemPrompt),
                        Map.of("role", "user", "content", "Patient condition: " + userCondition)
                )
        );

        try {
            JsonNode response = restClient.post()
                    .uri("/chat/completions")
                    .body(requestBody)
                    .retrieve()
                    .body(JsonNode.class);

            String jsonContent = response.path("choices").get(0).path("message").path("content").asText();
            return objectMapper.readValue(jsonContent, TriageGuidance.class);

        } catch (Exception e) {
            return new TriageGuidance(
                    "HIGH",
                    "Emergency Medicine",
                    List.of(
                            "Keep the patient calm, seated, or lying in a comfortable position.",
                            "Loosen tight clothing and ensure proper fresh airflow.",
                            "Do not give food or water if the person is drowsy or having trouble breathing.",
                            "Proceed to the nearest hospital listed or dial emergency services (112) immediately."
                    ),
                    "AI guidance temporarily unavailable. Seek immediate professional medical care."
            );
        }
    }
}
