package com.example.api.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.info.BuildProperties;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import java.time.Instant;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/health")
@RequiredArgsConstructor
public class HealthController {

    private final Optional<BuildProperties> buildProperties;
    private final Instant startTime = Instant.now();

    @GetMapping
    public Mono<Map<String, Object>> health() {
        long uptimeSeconds = Instant.now().getEpochSecond() - startTime.getEpochSecond();

        return Mono.just(Map.of(
                "status", "UP",
                "service", "api",
                "version", buildProperties.map(BuildProperties::getVersion).orElse("dev"),
                "uptime", formatUptime(uptimeSeconds),
                "timestamp", Instant.now().toString()
        ));
    }

    private static String formatUptime(long seconds) {
        long hours = seconds / 3600;
        long minutes = (seconds % 3600) / 60;
        long secs = seconds % 60;
        return String.format("%dh %dm %ds", hours, minutes, secs);
    }
}
