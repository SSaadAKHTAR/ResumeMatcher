package com.example.resumematcher;

import com.example.resumematcher.engine.MatcherEngine;
import com.example.resumematcher.model.JobPosting;
import com.example.resumematcher.model.Resume;
import com.example.resumematcher.strategy.KeywordMatchStrategy;
import com.example.resumematcher.nlp.ResumeNLPProcessor;
import com.example.resumematcher.utils.PDFResumeParser;
import org.apache.tika.exception.TikaException; // ✅ Required import


import java.io.IOException;
import java.util.Arrays;
import java.util.List;

public class TestRun {
    public static void main(String[] args) throws Exception {
        try {
            // 📄 1. Parse PDF resume from file
            // String filePath = "/home/saad/Desktop/oop/ResumeMatcher/project/src/main/resources/resumes/sample_resume.pdf";  // Adjust path if needed
            // String resumeText = PDFResumeParser.extractText(filePath);

            PDFResumeParser parser = new PDFResumeParser();
            String text = parser.extractText("/home/saad/Desktop/oop/ResumeMatcher/project/src/main/resources/resumes/sample_resume.pdf"); // your local test file
            System.out.println("Parsed Resume:\n" + text);


            // 👤 2. Create Resume object using parsed text
            Resume resume = new Resume("Ali", "ali@example.com", Arrays.asList("Java", "Spring", "Git"), text);

            // 📋 3. Create a sample job
            JobPosting job = new JobPosting("Java Dev", "Looking for a Java dev", Arrays.asList("Java", "Spring", "Docker"));

            // 🤖 4. Match score using strategy
            MatcherEngine engine = new MatcherEngine(new KeywordMatchStrategy());
            double score = engine.match(resume, job);
            System.out.println("Match Score: " + score);

            // 🧠 5. NLP skill extraction
            ResumeNLPProcessor nlp = new ResumeNLPProcessor();
            List<String> extractedSkills = nlp.extractKeywords(resume.getRawText());
            System.out.println("Extracted Skills: " + extractedSkills);

        } catch (IOException | TikaException e) {
            System.err.println("Failed to parse PDF: " + e.getMessage());
        }
    }
}
