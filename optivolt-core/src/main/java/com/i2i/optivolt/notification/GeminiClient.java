package com.i2i.optivolt.notification;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Component

public class GeminiClient {
    private static final String FALLBACK_TEXT =
            "Enerji tüketiminizde önemli bir değişiklik tespit edildi. " +
            "Şu anda otomatik öneri oluşturulamadı; lütfen kontrol panelinizden " +
            "cihaz bazlı tüketiminizi inceleyin.";

    private final HttpClient httpClient = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(5))
            .build();

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Value("${gemini.api.key:}")
    private String apiKey;

    @Value("${gemini.api.url:https://generativelanguage.googleapis.com/v1beta/models/gemini-2.0-flash:generateContent}")
    private String apiUrl;

    @Value("${gemini.enabled:true}")
    private boolean enabled;

    public String generateAdvisory(String prompt) {
        if (!enabled || apiKey == null || apiKey.isBlank()) {
            log.warn("Gemini disabled or GEMINI_API_KEY not set - returning fallback text");
            return FALLBACK_TEXT;
        }

        try {
            Map<String, Object> textPart = new LinkedHashMap<>();
            textPart.put("text", prompt);

            Map<String, Object> content = new LinkedHashMap<>();
            content.put("parts", List.of(textPart));

            Map<String, Object> body = new LinkedHashMap<>();
            body.put("contents", List.of(content));

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(apiUrl + "?key=" + apiKey))
                    .timeout(Duration.ofSeconds(10))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(objectMapper.writeValueAsString(body)))
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() != 200) {
                log.warn("Gemini API returned status {}: {}", response.statusCode(), response.body());
                return FALLBACK_TEXT;
            }

            return extractText(response.body());

        } catch (Exception e) {
            log.warn("Gemini API call failed, returning fallback text", e);
            return FALLBACK_TEXT;
        }
    }

    private String extractText(String responseBody) {
        try {
            JsonNode root = objectMapper.readTree(responseBody);
            JsonNode textNode = root.path("candidates").path(0)
                    .path("content").path("parts").path(0).path("text");
            if (textNode.isMissingNode() || textNode.asText().isBlank()) {
                return FALLBACK_TEXT;
            }
            return textNode.asText();
        } catch (Exception e) {
            log.warn("Could not parse Gemini response, returning fallback text", e);
            return FALLBACK_TEXT;
        }
    }
}
