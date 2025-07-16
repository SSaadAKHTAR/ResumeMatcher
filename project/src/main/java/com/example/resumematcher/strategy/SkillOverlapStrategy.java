package com.example.resumematcher.strategy;

import com.example.resumematcher.model.Resume;
import com.example.resumematcher.model.JobPosting;

import java.util.HashSet;
import java.util.Set;

public class SkillOverlapStrategy implements ScoringStrategy {
    @Override
    public double calculateScore(Resume resume, JobPosting job) {
        Set<String> resumeSkills = new HashSet<>(resume.getSkills());
        Set<String> jobSkills = new HashSet<>(job.getRequiredSkills());

        resumeSkills.retainAll(jobSkills); // Intersection
        int intersectionSize = resumeSkills.size();

        return (double) intersectionSize / (jobSkills.size() + 1); // Avoid divide-by-zero
    }
}
