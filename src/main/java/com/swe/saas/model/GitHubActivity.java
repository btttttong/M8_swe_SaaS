package com.swe.saas.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "github_activity")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GitHubActivity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String repositoryOwner;
    private String repositoryName;
    private String eventType; // Commit, Issue, Release
    @Column(columnDefinition = "TEXT")
    private String details;
    private LocalDateTime eventTimestamp;
}