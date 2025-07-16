package com.example.resumematcher.controller;

import com.example.resumematcher.engine.MatcherEngine;
import com.example.resumematcher.model.JobPosting;
import com.example.resumematcher.model.Resume;
import com.example.resumematcher.nlp.ResumeNLPProcessor;
import com.example.resumematcher.strategy.KeywordMatchStrategy;
import com.example.resumematcher.utils.PDFResumeParser;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

// import java.util.Arrays;
import java.util.List;

@RestController
@RequestMapping("/api/match")
public class ResumeMatcherController {

    private final MatcherEngine matcherEngine = new MatcherEngine(new KeywordMatchStrategy());
    private final ResumeNLPProcessor nlpProcessor = new ResumeNLPProcessor();
    private final PDFResumeParser pdfParser = new PDFResumeParser();

    @PostMapping("/resume")
    public ResponseEntity<?> matchResumeToJob(
            @RequestParam("file") MultipartFile file,
            @RequestParam("jobTitle") String jobTitle,
            @RequestParam("jobDescription") String jobDescription,
            @RequestParam("requiredSkills") List<String> requiredSkills
    ) {
        try {
            // Parse the uploaded PDF resume
            String resumeText = pdfParser.extractText(file);
            List<String> extractedSkills = nlpProcessor.extractKeywords(resumeText);

            Resume resume = new Resume("Uploaded User", "no-email@example.com", extractedSkills, resumeText);
            JobPosting job = new JobPosting(jobTitle, jobDescription, requiredSkills);

            double matchScore = matcherEngine.match(resume, job);

            return ResponseEntity.ok().body(
                new MatchResponse(matchScore, extractedSkills)
            );

        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Failed to process resume: " + e.getMessage());
        }
    }

    public static class MatchResponse {
        public double score;
        public List<String> extractedSkills;

        public MatchResponse(double score, List<String> extractedSkills) {
            this.score = score;
            this.extractedSkills = extractedSkills;
        }
    }
}
