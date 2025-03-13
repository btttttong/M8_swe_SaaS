package com.swe.saas.controller;

import com.swe.saas.dto.GitHubActivityDTO;
import com.swe.saas.model.RegisteredRepo;
import com.swe.saas.repository.RegisteredRepoRepository;
import com.swe.saas.service.GitHubActivityService;
import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/github/activity")
@RequiredArgsConstructor
public class GitHubActivityController {
    private final GitHubActivityService activityService;
    private final RegisteredRepoRepository registeredRepoRepository;

    @PostMapping("/register")
    public ResponseEntity<String> registerRepo(@RequestBody RegisteredRepo repository) {
        Optional<RegisteredRepo> existingRepo = registeredRepoRepository.findByOwnerAndName(repository.getOwner(), repository.getName());

        if (existingRepo.isPresent()) {
            return ResponseEntity.badRequest().body(" Repo already registered!");
        }

        registeredRepoRepository.save(repository);
        return ResponseEntity.ok(" Repo registered successfully!");
    }

    @GetMapping("/fetch")
    public ResponseEntity<?> fetchRecentActivities(
            @RequestParam String owner,
            @RequestParam String repo,
            @RequestHeader(value = "Authorization", required = false) String authHeader) {

        if (!registeredRepoRepository.findByOwnerAndName(owner, repo).isPresent()) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Repo is not registered. Please register first.");
        }

        List<GitHubActivityDTO> activities = activityService.getRecentActivities(owner, repo, authHeader);
        return ResponseEntity.ok(activities);
    }
    

    // @GetMapping("/fetch")
    // public ResponseEntity<List<GitHubActivityDTO>> fetchActivities(
    //         @RequestParam String owner,
    //         @RequestParam String repo,
    //         @RequestHeader("Authorization") String authHeader) {
        
    //     System.out.println("📥 Received Request in Spring Boot!");
    //     System.out.println("🔍 Owner: " + owner);
    //     System.out.println("🔍 Repo: " + repo);
    //     System.out.println("🔍 Authorization Header: " + authHeader);
    //     return ResponseEntity.ok(activityService.getRecentActivities(owner, repo, authHeader));
    // }

    @GetMapping("/stored")
    public ResponseEntity<List<GitHubActivityDTO>> getStoredActivities(
            @RequestParam String owner,
            @RequestParam String repo) {
        return ResponseEntity.ok(activityService.getStoredActivities(owner, repo));
    }


    @GetMapping("/test")
    public ResponseEntity<String> testEndpoint() {
        return ResponseEntity.ok(" API is working!");
    }

    @GetMapping("/stats")
    public ResponseEntity<?> getRepositoryStatistics() {
        return ResponseEntity.ok(activityService.getStatistics());
    }
}