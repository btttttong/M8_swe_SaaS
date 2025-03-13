package com.swe.saas.scheduler;

import com.swe.saas.service.GitHubActivityService;
import lombok.RequiredArgsConstructor;
import org.quartz.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class GitHubPollingJob implements Job {
    private final GitHubActivityService activityService;

    @Value("${github.api.url}")
    private String githubApiUrl;

    @Value("${github.token}")
    private String githubToken;

    @Override
    public void execute(JobExecutionContext context) {
        String owner = "btttttong";
        String repo = "M8_swe_SaaS";
        
        activityService.getRecentActivities(owner, repo, githubToken);
    }
}