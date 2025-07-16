package com.example.resumematcher.model;

import java.util.List;

public class Resume {
    private String name;
    private String email;
    private List<String> skills;
    private String rawText; // Full extracted text from resume

    // Constructors
    public Resume() {}
    public Resume(String name, String email, List<String> skills, String rawText) {
        this.name = name;
        this.email = email;
        this.skills = skills;
        this.rawText = rawText;
    }

    // Getters & Setters
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public List<String> getSkills() { return skills; }
    public void setSkills(List<String> skills) { this.skills = skills; }

    public String getRawText() { return rawText; }
    public void setRawText(String rawText) { this.rawText = rawText; }
}
