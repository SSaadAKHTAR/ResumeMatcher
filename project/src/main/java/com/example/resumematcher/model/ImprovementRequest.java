package com.example.resumematcher.model;

import java.util.List;

public class ImprovementRequest {
    private String resumeText;
    private String jobDescription;
    private List<String> extractedSkills;
    private List<String> requiredSkills;

    // Constructor
    public ImprovementRequest(String resumeText, String jobDescription, List<String> extractedSkills, List<String> requiredSkills) {
        this.resumeText = resumeText;
        this.jobDescription = jobDescription;
        this.extractedSkills = extractedSkills;
        this.requiredSkills = requiredSkills;
    }

    // Getters & Setters
    public String getResumeText() {
        return resumeText;
    }

    public void setResumeText(String resumeText) {
        this.resumeText = resumeText;
    }

    public String getJobDescription() {
        return jobDescription;
    }

    public void setJobDescription(String jobDescription) {
        this.jobDescription = jobDescription;
    }

    public List<String> getExtractedSkills() {
        return extractedSkills;
    }

    public void setExtractedSkills(List<String> extractedSkills) {
        this.extractedSkills = extractedSkills;
    }

    public List<String> getRequiredSkills() {
        return requiredSkills;
    }

    public void setRequiredSkills(List<String> requiredSkills) {
        this.requiredSkills = requiredSkills;
    }
}
