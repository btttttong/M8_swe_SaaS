package com.swe.saas.scheduler;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.swe.saas.model.Alert;
import com.swe.saas.model.RegisteredRepo;
import com.swe.saas.repository.AlertRepository;
import com.swe.saas.repository.RegisteredRepoRepository;
import com.swe.saas.service.EmailService;
import com.swe.saas.service.GitHubActivityService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class GitHubPollingJob {
    private final RegisteredRepoRepository registeredRepoRepository;
    private final AlertRepository alertRepository;
    private final GitHubActivityService activityService;
    private final EmailService emailService;

    @Scheduled(fixedRate = 300000) // Runs every 5 minutes
    public void pollGitHub() {
        System.out.println("⏳ Polling GitHub for new activity...");
        
        List<RegisteredRepo> registeredRepos = registeredRepoRepository.findAll();
        if (registeredRepos.isEmpty()) {
            System.out.println(" No registered repositories. Skipping polling.");
            return;
        }

        for (RegisteredRepo repo : registeredRepos) {
            System.out.println("🔍 Checking repo: " + repo.getOwner() + "/" + repo.getName());
            try {
                var activities = activityService.getRecentActivities(repo.getOwner(), repo.getName(), System.getenv("GITHUB_TOKEN"));

                // Check for alerts
                List<Alert> alerts = alertRepository.findByOwnerAndRepo(repo.getOwner(), repo.getName());
                for (Alert alert : alerts) {
                    for (var activity : activities) {
                        if (activity.getEventType().equalsIgnoreCase(alert.getEventType())) {
                            if (alert.getCondition() == null || activity.getDetails().contains(alert.getCondition())) {
                                System.out.println("Alert triggered for " + repo.getName() + " [" + alert.getEventType() + "]");

                                // ✅ Extract email from GitHub event details
                                String email = extractEmailFromDetails(activity.getDetails());
                                if (email != null) {
                                    emailService.sendAlertEmail(email, repo.getName(), alert.getEventType(), activity.getDetails());
                                } else {
                                    System.out.println(" No valid email found in event details.");
                                }
                            }
                        }
                    }
                }

            } catch (Exception e) {
                System.err.println("Failed to fetch activities for " + repo.getName() + ": " + e.getMessage());
            }
        }
    }

    // ✅ Extract email from event details
    private String extractEmailFromDetails(String detailsJson) {
        try {
            System.out.println("🔍 Raw JSON Before Fix: " + detailsJson);  // Debugging
    
            // ✅ Fix JSON formatting: Replace '=' with ':' and ensure proper quotes
            detailsJson = detailsJson.replace("=", ":").replace("'", "\"");
    
            System.out.println("🔍 Formatted JSON: " + detailsJson);  // Debugging
    
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode rootNode = objectMapper.readTree(detailsJson);
    
            // ✅ Extract email from `commit.author.email`
            String email = rootNode.path("commit").path("author").path("email").asText(null);
    
            System.out.println("📧 Extracted Email: " + email);
            return email;
        } catch (Exception e) {
            System.err.println("❌ Error extracting email: " + e.getMessage());
            return null;
        }
    }
}