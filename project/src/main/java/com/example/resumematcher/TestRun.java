package com.example.resumematcher;

import com.example.resumematcher.engine.MatcherEngine;
import com.example.resumematcher.model.JobPosting;
import com.example.resumematcher.model.Resume;
import com.example.resumematcher.strategy.KeywordMatchStrategy;
import com.example.resumematcher.nlp.ResumeNLPProcessor;
import com.example.resumematcher.utils.PDFResumeParser;
import org.apache.tika.exception.TikaException;

import java.io.IOException;
import java.util.List;

public class TestRun {
    public static void main(String[] args) throws Exception {
        try {
            // 📄 1. Parse PDF resume from file
            PDFResumeParser parser = new PDFResumeParser();
            String text = parser.extractText("/home/saad/Desktop/oop/ResumeMatcher/project/src/main/resources/resumes/sample_resume.pdf");
            System.out.println("Parsed Resume:\n" + text);

            // 🧠 2. Extract skills from parsed text using NLP
            ResumeNLPProcessor nlp = new ResumeNLPProcessor();
            List<String> extractedSkills = nlp.extractKeywords(text);
            System.out.println("Extracted Skills: " + extractedSkills);

            // 👤 3. Create Resume object using extracted skills
            Resume resume = new Resume("Ali", "ali@example.com", extractedSkills, text);

            // 📋 4. Create a sample job posting
            JobPosting job = new JobPosting("Java Dev", "Looking for a Java dev", List.of("Java", "Spring", "Docker", "Artificial Intelligence"));

            // 🤖 5. Compute match score using keyword matching strategy
            MatcherEngine engine = new MatcherEngine(new KeywordMatchStrategy());
            double score = engine.match(resume, job);
            System.out.println("Match Score: " + score);

        } catch (IOException | TikaException e) {
            System.err.println("Failed to parse PDF: " + e.getMessage());
        }
    }
}
