package com.example.resumematcher.controller;

import com.example.resumematcher.engine.MatcherEngine;
import com.example.resumematcher.model.JobPosting;
import com.example.resumematcher.model.Resume;
import com.example.resumematcher.nlp.ResumeNLPProcessor;
import com.example.resumematcher.strategy.KeywordMatchStrategy;
import com.example.resumematcher.utils.PDFResumeParser;
import com.example.resumematcher.service.ImprovementService;
import com.example.resumematcher.service.ImprovementService.ImprovementResult;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
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
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Autowired
    private ImprovementService improvementService;

    @GetMapping("/")
    public String index() {
        return "index";
    }

    @PostMapping("/resume")
    @ResponseBody
    public ResponseEntity<?> matchResumeToJob(
            @RequestParam("file") MultipartFile file,
            @RequestParam("jobTitle") String jobTitle,
            @RequestParam("jobDescription") String jobDescription,
            @RequestParam("requiredSkills") String  requiredSkillsJson
    ) {
        try {
            List<String> requiredSkillsList = objectMapper.readValue(
                requiredSkillsJson, new TypeReference<List<String>>() {}
        );


            String resumeText = pdfParser.extractText(file);
            List<String> extractedSkills = nlpProcessor.extractKeywords(resumeText);

            Resume resume = new Resume("Uploaded User", "no-email@example.com", extractedSkills, resumeText);
            JobPosting job = new JobPosting(jobTitle, jobDescription, requiredSkillsList);

            double matchScore = matcherEngine.match(resume, job);
            double percentageScore = matchScore * 100.0;

            return ResponseEntity.ok(new MatchResponse(percentageScore, extractedSkills));

        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Failed to process resume: " + e.getMessage());
        }
    }

    @PostMapping("/improvements")
    @ResponseBody
    public ResponseEntity<?> getResumeImprovements(
            @RequestParam("file") MultipartFile file,
            @RequestParam("jobTitle") String jobTitle,
            @RequestParam("jobDescription") String jobDescription,
            @RequestParam("requiredSkills") String requiredSkillsJson
    ) {
        try {
            System.out.println("📄 Received request for improvements");
            System.out.println("Job Title: " + jobTitle);

            // Parse requiredSkills JSON string
            List<String> requiredSkills = objectMapper.readValue(requiredSkillsJson, new TypeReference<List<String>>() {});
            System.out.println("Required Skills: " + requiredSkills);

            String resumeText = pdfParser.extractText(file);
            System.out.println("Extracted Resume Length: " + resumeText.length());

            List<String> extractedSkills = nlpProcessor.extractKeywords(resumeText);
            System.out.println("Extracted Skills: " + extractedSkills);

            Resume resume = new Resume("Uploaded User", "no-email@example.com", extractedSkills, resumeText);
            JobPosting job = new JobPosting(jobTitle, jobDescription, requiredSkills);

            double matchScore = matcherEngine.match(resume, job);
            double percentageScore = matchScore * 100.0;

            ImprovementResult result = improvementService.getImprovementSuggestions(
                    resumeText, jobTitle, jobDescription, requiredSkills
            );

            return ResponseEntity.ok(new ImprovementResponse(
                    percentageScore,
                    extractedSkills,
                    result.improvements,
                    result.missingSkills,
                    result.motivation
            ));

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity
                    .badRequest()
                    .body("{\"error\": \"" + e.getMessage().replace("\"", "'") + "\"}");
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

    public static class ImprovementResponse {
        public double score;
        public List<String> extractedSkills;
        public List<String> improvements;
        public List<String> missingSkills;
        public String motivation;

        public ImprovementResponse(double score, List<String> extractedSkills,
                                    List<String> improvements, List<String> missingSkills, String motivation) {
            this.score = score;
            this.extractedSkills = extractedSkills;
            this.improvements = improvements;
            this.missingSkills = missingSkills;
            this.motivation = motivation;
        }
    }
}
