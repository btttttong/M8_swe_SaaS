package com.swe.saas.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Alert {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    private String owner;
    private String repo;
    private String eventType;
    private String condition;

    private LocalDateTime createdAt = LocalDateTime.now();
}