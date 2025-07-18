package com.example.resumematcher.strategy;

import com.example.resumematcher.model.Resume;
import com.example.resumematcher.model.JobPosting;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class KeywordMatchStrategy implements ScoringStrategy {
    @Override
    public double calculateScore(Resume resume, JobPosting job) {
        // Normalize both to lowercase
        Set<String> resumeSkills = resume.getSkills().stream()
                .map(String::toLowerCase)
                .map(String::trim)
                .collect(Collectors.toSet());

        List<String> jobSkills = job.getRequiredSkills();

        long matchCount = jobSkills.stream()
                .map(String::toLowerCase)
                .map(String::trim)
                .filter(resumeSkills::contains)
                .count();

        return jobSkills.isEmpty() ? 0.0 : (double) matchCount / jobSkills.size();
    }
}
