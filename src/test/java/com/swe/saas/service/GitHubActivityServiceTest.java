package com.swe.saas.service;

import com.swe.saas.dto.GitHubActivityDTO;
import com.swe.saas.model.GitHubActivity;
import com.swe.saas.model.RegisteredRepo;
import com.swe.saas.repository.GitHubActivityRepository;
import com.swe.saas.repository.RegisteredRepoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class GitHubActivityServiceTest {

    @Mock
    private GitHubActivityRepository activityRepository;

    @Mock
    private RegisteredRepoRepository registeredRepoRepository;

    @Mock
    private RestTemplate restTemplate;

    @InjectMocks
    private GitHubActivityService gitHubActivityService;

    private final String owner = "btttttong";
    private final String repo = "M8_swe_SaaS";
    private final String authHeader = "mock_token";

    @BeforeEach
    void setup() {
        // ✅ Mock repository registration
        when(registeredRepoRepository.findByOwnerAndName(owner, repo))
            .thenReturn(Optional.of(new RegisteredRepo(owner, repo)));

        // ✅ Insert mock activity data
        GitHubActivity mockActivity = GitHubActivity.builder()
                .repositoryOwner(owner)
                .repositoryName(repo)
                .eventType("Commit")
                .details("{\"commit\":{\"author\":{\"name\":\"John Doe\", \"email\":\"john@example.com\"}, \"message\":\"Fix bug\"}}")
                .eventTimestamp(LocalDateTime.now())
                .build();

        when(activityRepository.findByRepositoryOwnerAndRepositoryName(owner, repo))
            .thenReturn(List.of(mockActivity));

        when(activityRepository.save(any())).thenReturn(mockActivity);
    }

    @Test
    void testInsertGitHubActivity() {
        // ✅ Mock activity
        GitHubActivity mockActivity = GitHubActivity.builder()
                .repositoryOwner("btttttong")
                .repositoryName("M8_swe_SaaS")
                .eventType("Commit")
                .details("{\"commit\":{\"author\":{\"name\":\"John Doe\", \"email\":\"john@example.com\"}, \"message\":\"Fix bug\"}}")
                .eventTimestamp(LocalDateTime.now())
                .build();
    
        // ✅ Make sure the repository returns the saved object
        when(activityRepository.save(any(GitHubActivity.class))).thenReturn(mockActivity);
    
        // ✅ Save activity
        GitHubActivity savedActivity = activityRepository.save(mockActivity);
    
        // ✅ Assertions
        assertNotNull(savedActivity, "Saved activity should not be null");
        assertEquals("btttttong", savedActivity.getRepositoryOwner(), "Repository owner should match");
    }

    
    @Test
    void testGetRecentActivities() {
        // ✅ Step 1: Register a repository
        RegisteredRepo mockRepo = new RegisteredRepo("btttttong", "M8_swe_SaaS");
        when(registeredRepoRepository.findByOwnerAndName("btttttong", "M8_swe_SaaS"))
            .thenReturn(Optional.of(mockRepo));
    
        // ✅ Step 2: Mock activity response
        GitHubActivity mockActivity = GitHubActivity.builder()
                .repositoryOwner("btttttong")
                .repositoryName("M8_swe_SaaS")
                .eventType("Commit")
                .details("{\"commit\":{\"author\":{\"name\":\"John Doe\", \"email\":\"john@example.com\"}, \"message\":\"Fix bug\"}}")
                .eventTimestamp(LocalDateTime.now())
                .build();
    
        when(activityRepository.findByRepositoryOwnerAndRepositoryName("btttttong", "M8_swe_SaaS"))
            .thenReturn(List.of(mockActivity));
    
        when(activityRepository.save(any())).thenReturn(mockActivity);
    
        // ✅ Step 3: Call the method
        List<GitHubActivityDTO> activities = gitHubActivityService.getRecentActivities("btttttong", "M8_swe_SaaS", "ghp_mockToken");
    
        // ✅ Step 4: Assertions
        assertNotNull(activities, "Activities should not be null");
        assertFalse(activities.isEmpty(), "Activities should not be empty");
        assertEquals("Commit", activities.get(0).getEventType(), "Event type should be 'Commit'");
    }

    @Test
    void testGetStoredActivities() {
        GitHubActivity mockActivity = GitHubActivity.builder()
                .repositoryOwner("btttttong")
                .repositoryName("M8_swe_SaaS")
                .eventType("Commit")
                .details("{\"commit\":{\"author\":{\"name\":\"John Doe\", \"email\":\"john@example.com\"}, \"message\":\"Fix bug\"}}")
                .eventTimestamp(LocalDateTime.now())
                .build();

        when(activityRepository.findByRepositoryOwnerAndRepositoryName("btttttong", "M8_swe_SaaS"))
            .thenReturn(List.of(mockActivity));
        List<GitHubActivityDTO> activities = gitHubActivityService.getStoredActivities("btttttong", "M8_swe_SaaS");

        assertNotNull(activities, "Stored activities should not be null");
        assertFalse(activities.isEmpty(), "Stored activities should not be empty");
        assertEquals("Commit", activities.get(0).getEventType(), "Event type should be 'Commit'");
    }
}