package com.academic.sastracare.service;

import com.academic.sastracare.exception.external.ExternalServiceException;
import com.academic.sastracare.exception.validation.InvalidRequestException;
import io.netty.channel.ChannelOption;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;
import reactor.netty.http.client.HttpClient;

import java.io.InputStream;
import java.time.Duration;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class HuggingFaceTtsService {

    @Value("${huggingface.api-key}")
    private String apiKey;

    @Value("${huggingface.tts-url}")
    private String ttsUrl;

    private final WebClient.Builder webClientBuilder;

    private WebClient buildWebClient() {

        HttpClient httpClient = HttpClient.create()
                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 10000)
                .responseTimeout(Duration.ofSeconds(60));

        return webClientBuilder
                .clientConnector(new ReactorClientHttpConnector(httpClient))
                .build();
    }

    public byte[] convertTextToSpeech(String text, String lang) {

        if (text == null || text.isBlank()) {
            throw new InvalidRequestException("Text cannot be null or empty");
        }

        // 🔥 Normalize Accept-Language header
        if (lang != null && lang.contains(",")) {
            lang = lang.split(",")[0];
        }

        if (lang != null && lang.contains("-")) {
            lang = lang.split("-")[0];
        }

        if (lang == null || lang.isBlank()) {
            lang = "en";
        }

        try {

            WebClient webClient = buildWebClient();

            // ✅ FIXED: send "text" and "language" (not "inputs")
            Map<String, Object> payload = Map.of(
                    "text", text,
                    "language", lang
            );

            byte[] response = webClient.post()
                    .uri(ttsUrl)
                    .header(HttpHeaders.AUTHORIZATION, "Bearer " + apiKey)
                    .contentType(MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_OCTET_STREAM, MediaType.APPLICATION_JSON)
                    .bodyValue(payload)
                    .retrieve()
                    .onStatus(
                            status -> status.isError(),
                            clientResponse ->
                                    clientResponse.bodyToMono(String.class)
                                            .flatMap(body -> Mono.error(
                                                    new ExternalServiceException(
                                                            "TTS HTTP error: " + body
                                                    )
                                            ))
                    )
                    .bodyToMono(byte[].class)
                    .timeout(Duration.ofSeconds(65))
                    .block();

            if (response == null || response.length == 0) {
                throw new ExternalServiceException("TTS returned empty audio");
            }

            return response;

        } catch (WebClientResponseException e) {
            throw new ExternalServiceException(
                    "TTS HTTP status error: " + e.getStatusCode(),
                    e
            );
        } catch (Exception e) {
            throw new ExternalServiceException(
                    "TTS processing failure: " + e.getMessage(),
                    e
            );
        }
    }

    private boolean isLikelyMp3(byte[] data) {
        if (data.length < 3) return false;

        return (data[0] == (byte) 0xFF && data[1] == (byte) 0xFB)
                || (data[0] == 'I' && data[1] == 'D' && data[2] == '3');
    }

    public byte[] getFallbackAudio() {
        try {
            ClassPathResource resource =
                    new ClassPathResource("audio/fallback.mp3");
            try (InputStream in = resource.getInputStream()) {
                return in.readAllBytes();
            }
        } catch (Exception e) {
            throw new RuntimeException("Fallback audio missing", e);
        }
    }
}