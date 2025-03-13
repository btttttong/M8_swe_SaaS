package com.swe.saas.controller;

import com.swe.saas.model.Alert;
import com.swe.saas.repository.AlertRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/alerts")
@RequiredArgsConstructor
public class AlertController {
    private final AlertRepository alertRepository;

    @PostMapping("/register")
    public ResponseEntity<String> registerAlert(@RequestBody Alert alert) {
        alertRepository.save(alert);
        return ResponseEntity.ok("✅ Alert registered successfully!");
    }

    @GetMapping("/list")
    public ResponseEntity<List<Alert>> listAlerts() {
        return ResponseEntity.ok(alertRepository.findAll());
    }
}