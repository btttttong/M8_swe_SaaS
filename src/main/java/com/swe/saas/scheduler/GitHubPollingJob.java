package com.swe.saas.scheduler;

import com.fasterxml.jackson.core.JsonProcessingException;
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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

@Component
@RequiredArgsConstructor
public class GitHubPollingJob {
    private final RegisteredRepoRepository registeredRepoRepository;
    private final AlertRepository alertRepository;
    private final GitHubActivityService activityService;
    private final EmailService emailService;
    private static final Logger logger = LoggerFactory.getLogger(GitHubPollingJob.class);

    @Scheduled(fixedRate = 300000) // Runs every 5 minutes
    public void pollGitHub() {
        logger.info("⏳ Polling GitHub for new activity...");
        
        List<RegisteredRepo> registeredRepos = registeredRepoRepository.findAll();
        if (registeredRepos.isEmpty()) {
            logger.info(" No registered repositories. Skipping polling.");
            return;
        }

        for (RegisteredRepo repo : registeredRepos) {
            // System.out.println("🔍 Checking repo: " + repo.getOwner() + "/" + repo.getName());
            logger.info("Checking repo: " + repo.getOwner() + "/" + repo.getName());
            try {
                var activities = activityService.getRecentActivities(repo.getOwner(), repo.getName(), System.getenv("GITHUB_TOKEN"));

                // Check for alerts
                List<Alert> alerts = alertRepository.findByOwnerAndRepo(repo.getOwner(), repo.getName());
                for (Alert alert : alerts) {
                    for (var activity : activities) {
                        if (activity.getEventType().equalsIgnoreCase(alert.getEventType())) {
                            if (alert.getCondition() == null || activity.getDetails().contains(alert.getCondition())) {
                                // System.out.println("Alert triggered for " + repo.getName() + " [" + alert.getEventType() + "]");
                                logger.info("Alert triggered for " + repo.getName() + " [" + alert.getEventType() + "]");

                                // ✅ Extract email from GitHub event details
                                String email = extractEmailFromDetails(activity.getDetails());
                                if (email != null) {
                                    emailService.sendAlertEmail(email, repo.getName(), alert.getEventType(), activity.getDetails());
                                } else {
                                    // System.out.println(" No valid email found in event details.");
                                    logger.error("No valid email found in event details.");
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

    private String extractEmailFromDetails(String detailsJson) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode rootNode = objectMapper.readTree(detailsJson);

            // ✅ Try to extract email from `author` field
            JsonNode authorNode = rootNode.path("author");
            String email = authorNode.path("email").asText(null);

            if (email == null || email.isEmpty()) {
                // ✅ Try to extract email from `committer` field as a fallback
                JsonNode committerNode = rootNode.path("committer");
                email = committerNode.path("email").asText(null);
            }

            System.out.println("🔍 Extracted Email: " + email);
            return email;
        } catch (Exception e) {
            System.err.println("Error extracting email: " + e.getMessage());
            return null;
        }
    }
}