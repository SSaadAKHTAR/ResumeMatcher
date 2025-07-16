package com.example.resumematcher.engine;

import com.example.resumematcher.model.Resume;
import com.example.resumematcher.model.JobPosting;
import com.example.resumematcher.strategy.ScoringStrategy;

public class MatcherEngine {
    private ScoringStrategy strategy;

    public MatcherEngine(ScoringStrategy strategy) {
        this.strategy = strategy;
    }

    public void setStrategy(ScoringStrategy strategy) {
        this.strategy = strategy;
    }

    public double match(Resume resume, JobPosting jobPosting) {
        return strategy.calculateScore(resume, jobPosting);
    }
}
