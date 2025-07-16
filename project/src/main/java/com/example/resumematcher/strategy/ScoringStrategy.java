package com.example.resumematcher.strategy;

import com.example.resumematcher.model.Resume;
import com.example.resumematcher.model.JobPosting;

public interface ScoringStrategy {
    double calculateScore(Resume resume, JobPosting job);
}
