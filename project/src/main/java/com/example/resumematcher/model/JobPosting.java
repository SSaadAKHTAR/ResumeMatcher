package com.example.resumematcher.model;

import java.util.List;

public class JobPosting {
    private String title;
    private String description;
    private List<String> requiredSkills;

    public JobPosting() {}
    public JobPosting(String title, String description, List<String> requiredSkills) {
        this.title = title;
        this.description = description;
        this.requiredSkills = requiredSkills;
    }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public List<String> getRequiredSkills() { return requiredSkills; }
    public void setRequiredSkills(List<String> requiredSkills) { this.requiredSkills = requiredSkills; }
}
