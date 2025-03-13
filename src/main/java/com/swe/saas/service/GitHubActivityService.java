package com.swe.saas.service;

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
import java.util.LinkedHashMap;
import com.fasterxml.jackson.core.type.TypeReference;

@Service
@RequiredArgsConstructor
public class GitHubActivityService {
    private final GitHubActivityRepository activityRepository;
    private final RestTemplate restTemplate = new RestTemplate();

    public List<GitHubActivityDTO> getRecentActivities(String owner, String repo, String authHeader) {
        String url = String.format("https://api.github.com/repos/%s/%s/commits", owner, repo);
        
        HttpHeaders headers = new HttpHeaders();
        
        if (!authHeader.startsWith("Bearer ")) {
            headers.setBearerAuth(authHeader);
        } else {
            headers.set("Authorization", authHeader);
        }
        headers.set("User-Agent", "SpringBoot-GitHub-Client");
        headers.set("Accept", "application/vnd.github.v3+json");
    
        HttpEntity<String> entity = new HttpEntity<>(headers);
        ObjectMapper objectMapper = new ObjectMapper();

        // System.out.println("Fetching recent activities from GitHub API..." + headers + " " + url);
    
        try {
            ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, entity, String.class);
    
            if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
                // ✅ FIX: Explicitly define type reference
                List<LinkedHashMap<String, Object>> eventList = objectMapper.readValue(
                    response.getBody(),
                    new TypeReference<List<LinkedHashMap<String, Object>>>() {} 
                );
    
                return eventList.stream().map(eventMap -> {
                    String eventType = "Commit";
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
            System.out.println("GitHub API Error: " + e.getResponseBodyAsString());
        } catch (Exception e) {
            System.out.println("Unexpected Error: " + e.getMessage());
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
}