package com.swe.saas.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.swe.saas.dto.GitHubActivityDTO;
import com.swe.saas.model.GitHubActivity;
import com.swe.saas.repository.GitHubActivityRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import com.fasterxml.jackson.core.type.TypeReference;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
@RequiredArgsConstructor
public class GitHubActivityService {
    private final GitHubActivityRepository activityRepository;
    private final RestTemplate restTemplate = new RestTemplate();
    private static final Logger logger = LoggerFactory.getLogger(GitHubActivityService.class);

    public List<GitHubActivityDTO> getRecentActivities(String owner, String repo, String authHeader) {
        List<GitHubActivityDTO> allActivities = new ArrayList<>();
        
        // Fetch commits
        allActivities.addAll(fetchGitHubData(owner, repo, authHeader, "commits", "Commit"));

        // Fetch issues
        allActivities.addAll(fetchGitHubData(owner, repo, authHeader, "issues", "Issue"));

        // Fetch releases
        allActivities.addAll(fetchGitHubData(owner, repo, authHeader, "releases", "Release"));

        return allActivities;
    }

    /**
     * Fetches GitHub API data (Commits, Issues, Releases)
     */
    private List<GitHubActivityDTO> fetchGitHubData(String owner, String repo, String authHeader, String endpoint, String eventType) {
        String url = String.format("https://api.github.com/repos/%s/%s/%s", owner, repo, endpoint);
        
        HttpHeaders headers = new HttpHeaders();
        if (!authHeader.startsWith("Bearer ")) {
            headers.setBearerAuth(authHeader);
        } else {
            headers.set("Authorization", authHeader);
        }
        headers.set("User-Agent", "SpringBoot-GitHub-Client");
        headers.set("Accept", "application/vnd.github.v3+json");

        // System.out.println("🔗 Fetching data from: " + url);
        // System.out.println("🔑  Header: " + headers);

        HttpEntity<String> entity = new HttpEntity<>(headers);
        ObjectMapper objectMapper = new ObjectMapper();
        
        try {
            ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, entity, String.class);
            if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
                List<LinkedHashMap<String, Object>> eventList = objectMapper.readValue(
                    response.getBody(),
                    new TypeReference<List<LinkedHashMap<String, Object>>>() {}
                );

                return eventList.stream().map(eventMap -> {
                    String details = eventMap.toString();
                    GitHubActivity activity = GitHubActivity.builder()
                            .repositoryOwner(owner)
                            .repositoryName(repo)
                            .eventType(eventType)
                            .details(details)
                            .eventTimestamp(LocalDateTime.now())
                            .build();
                    activityRepository.save(activity);
                    return new GitHubActivityDTO(owner, repo, eventType, details, LocalDateTime.now());
                }).toList();
            }
        } catch (HttpClientErrorException e) {
            if (e.getStatusCode() == HttpStatus.FORBIDDEN) {
                handleRateLimit(e);
            } else {
                // System.err.println("GitHub API Error: " + e.getResponseBodyAsString());
                logger.error("GitHub API Error: {}", e.getResponseBodyAsString());
            }
        } catch (Exception e) {
            // System.out.println("Unexpected Error: " + e.getMessage());
            logger.error("GitHub API Error: {}", e.getMessage());
            e.printStackTrace();
        }

        return List.of();
    }

    public List<GitHubActivityDTO> getStoredActivities(String owner, String repo) {
        return activityRepository.findByRepositoryOwnerAndRepositoryName(owner, repo)
                .stream()
                .map(a -> new GitHubActivityDTO(a.getRepositoryOwner(), a.getRepositoryName(), a.getEventType(), a.getDetails(), a.getEventTimestamp()))
                .collect(Collectors.toList());
    }


    public Map<String, Object> getStatistics() {
        List<GitHubActivity> activities = activityRepository.findAll();
        
        if (activities.isEmpty()) {
            return Map.of("message", "No activity data found.");
        }

        Map<String, Long> eventCounts = activities.stream()
            .collect(Collectors.groupingBy(GitHubActivity::getEventType, Collectors.counting()));

        Map<String, Long> repoCommitCounts = activities.stream()
            .filter(a -> "Commit".equalsIgnoreCase(a.getEventType()))
            .collect(Collectors.groupingBy(a -> a.getRepositoryOwner() + "/" + a.getRepositoryName(), Collectors.counting()));

        String mostActiveRepo = repoCommitCounts.entrySet().stream()
            .max(Map.Entry.comparingByValue())
            .map(Map.Entry::getKey)
            .orElse("N/A");
    
        Map<String, Long> userCommitCounts = activities.stream()
            .filter(a -> "Commit".equalsIgnoreCase(a.getEventType()))
            .collect(Collectors.groupingBy(a -> extractAuthorFromDetails(a.getDetails()), Collectors.counting()));

        String mostActiveUser = userCommitCounts.entrySet().stream()
            .max(Map.Entry.comparingByValue())
            .map(Map.Entry::getKey)
            .orElse("N/A");

        return Map.of(
            "totalCommits", eventCounts.getOrDefault("Commit", 0L),
            "totalPullRequests", eventCounts.getOrDefault("PullRequest", 0L),
            "totalIssues", eventCounts.getOrDefault("Issue", 0L),
            "mostActiveRepo", mostActiveRepo,
            "mostActiveUser", mostActiveUser
        );
    }
    

    private String extractAuthorFromDetails(String details) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode rootNode = objectMapper.readTree(details);
            return rootNode.path("commit").path("author").path("name").asText("Unknown");
        } catch (Exception e) {
            return "Unknown";
        }
    }

    private void handleRateLimit(HttpClientErrorException e) {
        HttpHeaders headers = e.getResponseHeaders();
        if (headers != null) {
            String remaining = headers.getFirst("X-RateLimit-Remaining");
            String resetTime = headers.getFirst("X-RateLimit-Reset");
    
            if ("0".equals(remaining) && resetTime != null) {
                long waitTime = Long.parseLong(resetTime) * 1000 - System.currentTimeMillis();
                waitTime = Math.max(waitTime, 5000);
    
                // System.out.println("GitHub API Rate Limit Reached! Retrying after " + (waitTime / 1000) + " seconds...");
                logger.info("GitHub API Rate Limit Reached! Retrying after {} seconds...", (waitTime / 1000));
                try {
                    Thread.sleep(waitTime);
                } catch (InterruptedException ex) {
                    Thread.currentThread().interrupt();
                }
            }
        }
    }
}