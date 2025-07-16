package com.example.resumematcher.strategy;

import com.example.resumematcher.model.Resume;
import com.example.resumematcher.model.JobPosting;

import java.util.List;

public class KeywordMatchStrategy implements ScoringStrategy {
    @Override
    public double calculateScore(Resume resume, JobPosting job) {
        List<String> resumeSkills = resume.getSkills();
        List<String> jobSkills = job.getRequiredSkills();

        long matchCount = resumeSkills.stream()
                .filter(jobSkills::contains)
                .count();

        return (double) matchCount / jobSkills.size();  // Score between 0.0 and 1.0
    }
}
