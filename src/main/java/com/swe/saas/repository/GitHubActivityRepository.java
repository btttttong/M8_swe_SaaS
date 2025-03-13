package com.swe.saas.repository;

import com.swe.saas.model.GitHubActivity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface GitHubActivityRepository extends JpaRepository<GitHubActivity, Long> {
    List<GitHubActivity> findByRepositoryOwnerAndRepositoryName(String owner, String name);
}