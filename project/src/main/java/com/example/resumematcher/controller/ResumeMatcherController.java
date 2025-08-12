package com.example.resumematcher.controller;

import com.example.resumematcher.engine.MatcherEngine;
import com.example.resumematcher.model.JobPosting;
import com.example.resumematcher.model.Resume;
import com.example.resumematcher.nlp.ResumeNLPProcessor;
import com.example.resumematcher.strategy.KeywordMatchStrategy;
import com.example.resumematcher.utils.PDFResumeParser;
import com.example.resumematcher.service.ImprovementService;
import com.example.resumematcher.service.ImprovementService.ImprovementResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Controller
@RequestMapping("/api/match")
public class ResumeMatcherController {

    private final MatcherEngine matcherEngine = new MatcherEngine(new KeywordMatchStrategy());
    private final ResumeNLPProcessor nlpProcessor = new ResumeNLPProcessor();
    private final PDFResumeParser pdfParser = new PDFResumeParser();

    @Autowired
    private ImprovementService improvementService;

    // Serve the UI
    @GetMapping("/")
    public String index() {
        return "index";
    }

    // Fast scoring only
    @PostMapping("/resume")
    @ResponseBody
    public ResponseEntity<?> matchResumeToJob(
            @RequestParam("file") MultipartFile file,
            @RequestParam("jobTitle") String jobTitle,
            @RequestParam("jobDescription") String jobDescription,
            @RequestParam("requiredSkills") List<String> requiredSkills
    ) {
        try {
            String resumeText = pdfParser.extractText(file);
            List<String> extractedSkills = nlpProcessor.extractKeywords(resumeText);

            Resume resume = new Resume("Uploaded User", "no-email@example.com", extractedSkills, resumeText);
            JobPosting job = new JobPosting(jobTitle, jobDescription, requiredSkills);

            double matchScore = matcherEngine.match(resume, job);

            return ResponseEntity.ok(new MatchResponse(matchScore, extractedSkills));

        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Failed to process resume: " + e.getMessage());
        }
    }

    // Scoring + LLM suggestions
    @PostMapping("/improvements")
    @ResponseBody
    public ResponseEntity<?> getResumeImprovements(
            @RequestParam("file") MultipartFile file,
            @RequestParam("jobTitle") String jobTitle,
            @RequestParam("jobDescription") String jobDescription,
            @RequestParam("requiredSkills") List<String> requiredSkills
    ) {
        try {
            System.out.println("📄 Received request for improvements");
            System.out.println("Job Title: " + jobTitle);
            System.out.println("Required Skills: " + requiredSkills);

            // Extract text from resume
            String resumeText = pdfParser.extractText(file);
            System.out.println("Extracted Resume Length: " + resumeText.length());

            // Extract skills from resume text
            List<String> extractedSkills = nlpProcessor.extractKeywords(resumeText);
            System.out.println("Extracted Skills: " + extractedSkills);

            // Build objects
            Resume resume = new Resume("Uploaded User", "no-email@example.com", extractedSkills, resumeText);
            JobPosting job = new JobPosting(jobTitle, jobDescription, requiredSkills);

            // Calculate match score
            double matchScore = matcherEngine.match(resume, job);
            System.out.println("Match Score: " + matchScore);

            // Get suggestions from LLM (Gemini / HuggingFace)
            ImprovementResult result = improvementService.getImprovementSuggestions(
                    resumeText, jobTitle, jobDescription, requiredSkills
            );

            // Return successful response
            return ResponseEntity.ok(new ImprovementResponse(
                    matchScore,
                    extractedSkills,
                    result.improvements,
                    result.missingSkills,
                    result.motivation
            ));

        } catch (Exception e) {
            e.printStackTrace(); // Logs error to backend console
            return ResponseEntity
                    .badRequest()
                    .body("{\"error\": \"" + e.getMessage().replace("\"", "'") + "\"}");
        }
    }

    // ---------------- DTO Classes ----------------
    public static class MatchResponse {
        public double score;
        public List<String> extractedSkills;

        public MatchResponse(double score, List<String> extractedSkills) {
            this.score = score;
            this.extractedSkills = extractedSkills;
        }
    }

    public static class ImprovementResponse {
        public double score;
        public List<String> extractedSkills;
        public String improvements;
        public String missingSkills;
        public String motivation;

        public ImprovementResponse(double score, List<String> extractedSkills,
                                    String improvements, String missingSkills, String motivation) {
            this.score = score;
            this.extractedSkills = extractedSkills;
            this.improvements = improvements;
            this.missingSkills = missingSkills;
            this.motivation = motivation;
        }
    }
}
