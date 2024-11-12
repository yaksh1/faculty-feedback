package com.yaksh.user_security.google;

import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.HashMap;
import java.util.Map;

@Service
public class PerspectiveApiService {

    private final RestTemplate restTemplate = new RestTemplate();

    @Value("${google.perspective.api.key}")
    private String apiKey;

    private final String apiUrl = "https://commentanalyzer.googleapis.com/v1alpha1/comments:analyze";

    public boolean isInappropriateReview(String reviewText) {
        double toxicityThreshold = 0.7;
        double insultThreshold = 0.6;
        double profanityThreshold = 0.6;
        double threatThreshold = 0.6;

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        // JSON payload for Perspective API request
        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("comment", Map.of("text", reviewText));
        requestBody.put("languages", new String[]{"en"});
        requestBody.put("requestedAttributes", Map.of(
                "TOXICITY", new HashMap<>(),
                "INSULT", new HashMap<>(),
                "THREAT", new HashMap<>(),
                "PROFANITY", new HashMap<>()
        ));

        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);

        String uri = UriComponentsBuilder.fromHttpUrl(apiUrl)
                .queryParam("key", apiKey)
                .toUriString();

        try {
            Map<String, Object> response = restTemplate.exchange(uri, HttpMethod.POST, entity, Map.class).getBody();

            Map<String, Object> attributeScores = (Map<String, Object>) response.get("attributeScores");

            double toxicityScore = getScore(attributeScores, "TOXICITY");
            double insultScore = getScore(attributeScores, "INSULT");
            double profanityScore = getScore(attributeScores, "PROFANITY");
            double threatScore = getScore(attributeScores, "THREAT");

            // Check if review exceeds any threshold
            return toxicityScore >= toxicityThreshold ||
                    insultScore >= insultThreshold ||
                    profanityScore >= profanityThreshold ||
                    threatScore >= threatThreshold;

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    private double getScore(Map<String, Object> attributeScores, String attribute) {
        Map<String, Object> scoreMap = (Map<String, Object>) attributeScores.get(attribute);
        return (double) ((Map<String, Object>) scoreMap.get("summaryScore")).get("value");
    }
}