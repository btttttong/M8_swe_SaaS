package com.swe.saas.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RegisteredRepo {  
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    private String owner;
    private String name;
    private LocalDateTime trackedSince = LocalDateTime.now();

    public RegisteredRepo(String owner, String name) {
        this.owner = owner;
        this.name = name;
    }
}