package com.swe.saas.repository;

import com.swe.saas.model.Alert;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface AlertRepository extends JpaRepository<Alert, UUID> {
    List<Alert> findByOwnerAndRepo(String owner, String repo);
}