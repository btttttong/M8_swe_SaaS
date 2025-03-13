package com.swe.saas.repository;

import com.swe.saas.model.RegisteredRepo;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface RegisteredRepoRepository extends JpaRepository<RegisteredRepo, UUID> {
    Optional<RegisteredRepo> findByOwnerAndName(String owner, String name);
}