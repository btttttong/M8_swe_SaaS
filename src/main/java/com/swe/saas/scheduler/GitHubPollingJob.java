package com.swe.saas.scheduler;

import com.swe.saas.model.RegisteredRepo;
import com.swe.saas.repository.RegisteredRepoRepository;
import com.swe.saas.service.GitHubActivityService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class GitHubPollingJob {
    private final RegisteredRepoRepository registeredRepoRepository;
    private final GitHubActivityService activityService;

    @Scheduled(fixedRate = 300000) // Runs every 5 minutes
    public void pollGitHub() {
        System.out.println("⏳ Polling GitHub for new activity...");
        
        List<RegisteredRepo> registeredRepos = registeredRepoRepository.findAll();
        if (registeredRepos.isEmpty()) {
            System.out.println("No registered repositories. Skipping polling.");
            return;
        }

        for (RegisteredRepo repo : registeredRepos) {
            System.out.println("🔍 Checking repo: " + repo.getOwner() + "/" + repo.getName());
            try {
                activityService.getRecentActivities(repo.getOwner(), repo.getName(), System.getenv("GITHUB_TOKEN"));
            } catch (Exception e) {
                System.err.println("Failed to fetch activities for " + repo.getName() + ": " + e.getMessage());
            }
        }
    }
}