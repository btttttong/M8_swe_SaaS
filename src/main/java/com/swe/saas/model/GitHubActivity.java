package com.swe.saas.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
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
    private String eventType; // commit, issue, PR, release
    private String details;
    private LocalDateTime eventTimestamp;
}