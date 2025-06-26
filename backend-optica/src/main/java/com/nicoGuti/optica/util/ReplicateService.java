package com.nicoGuti.optica.util;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class ReplicateService {

    private final WebClient client;

    public ReplicateService() {
        this.client = WebClient.builder()
                .baseUrl("https://api.replicate.com/v1")
                .defaultHeader("Content-Type", "application/json")
                .build();
    }

    public byte[] generarSuperposicion(String base64Cliente, String modifier) throws InterruptedException {
        Map<String, Object> input = new HashMap<>();
        input.put("image", "data:image/png;base64," + base64Cliente);
        input.put("modifier", modifier);

        Map<String, Object> requestBody = Map.of(
                "version", "a3fbd4da8fc3f5e8d6c81bba1de52e58569c723aad6c3dfba1f4283c8965860b",
                "input", input
        );

        Map<String, Object> prediction = client.post()
                .uri("/predictions")
                .bodyValue(requestBody)
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<Map<String, Object>>() {})
                .block();

        String predictionUrl = (String) ((Map<String, Object>) prediction.get("urls")).get("get");
        String status = (String) prediction.get("status");

        while (status.equals("starting") || status.equals("processing")) {
            Thread.sleep(1000);
            prediction = client.get()
                    .uri(predictionUrl)
                    .retrieve()
                    .bodyToMono(new ParameterizedTypeReference<Map<String, Object>>() {})
                    .block();
            status = (String) prediction.get("status");
        }

        if ("succeeded".equals(status)) {
            List<String> output = (List<String>) prediction.get("output");
            String imageUrl = output.get(0);
            return WebClient.create()
                    .get()
                    .uri(imageUrl)
                    .retrieve()
                    .bodyToMono(byte[].class)
                    .block();
        }

        throw new RuntimeException("La predicción falló con estado: " + status);
    }
}
