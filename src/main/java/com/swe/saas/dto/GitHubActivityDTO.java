package com.swe.saas.dto;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GitHubActivityDTO {
    private String repositoryOwner;
    private String repositoryName;
    private String eventType;
    private String details;
    private LocalDateTime eventTimestamp;
}