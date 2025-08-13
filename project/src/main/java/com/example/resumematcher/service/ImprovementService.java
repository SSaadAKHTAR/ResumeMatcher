package com.example.resumematcher.service;

import okhttp3.*;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Service
public class ImprovementService {

    @Value("${gemini.api.key}")
    private String geminiApiKey; // Loaded from application.properties

    private static final String GEMINI_API_URL =
            "https://generativelanguage.googleapis.com/v1beta/models/gemini-2.0-flash:generateContent";

    public static class ImprovementResult {
        public List<String> improvements;
        public List<String> missingSkills;
        public String motivation;

        public ImprovementResult(List<String> improvements, List<String> missingSkills, String motivation) {
            this.improvements = improvements;
            this.missingSkills = missingSkills;
            this.motivation = motivation;
        }
    }

    public ImprovementResult getImprovementSuggestions(
            String resume,
            String jobTitle,
            String jobDescription,
            List<String> requiredSkills
    ) throws IOException {

        String prompt = String.format("""
            You are an AI career advisor. The user has applied for the job: %s.
            Job description: %s
            Required skills: %s
            Resume content: %s

            Based on the resume and job requirements:
            1. Suggest specific skills or experiences the user should improve (as a JSON array of strings).
            2. Highlight key skills the user lacks compared to the job requirements (as a JSON array of strings).
            3. Provide a short motivational message to encourage the user.
            Respond strictly in JSON format with keys: improvements, missingSkills, motivation.
            """, jobTitle, jobDescription, requiredSkills, resume);

        OkHttpClient client = new OkHttpClient();

        JSONObject requestJson = new JSONObject()
                .put("contents", new JSONArray()
                        .put(new JSONObject()
                                .put("parts", new JSONArray()
                                        .put(new JSONObject().put("text", prompt))
                                )
                        )
                );

        Request request = new Request.Builder()
                .url(GEMINI_API_URL + "?key=" + geminiApiKey)
                .post(RequestBody.create(requestJson.toString(), MediaType.parse("application/json")))
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful())
                throw new IOException("Gemini API call failed: " + response.code() + " - " + response.message());

            String jsonResponse = response.body().string();

            // ✅ Save to log file for debugging
            try (FileWriter writer = new FileWriter("gemini_log.txt", true)) {
                writer.write("\n=== Gemini API Response ===\n");
                writer.write(jsonResponse + "\n");
            }

            JSONObject obj = new JSONObject(jsonResponse);
            String aiText = obj
                    .getJSONArray("candidates")
                    .getJSONObject(0)
                    .getJSONObject("content")
                    .getJSONArray("parts")
                    .getJSONObject(0)
                    .getString("text")
                    .trim();

            JSONObject aiJson;
            try {
                aiJson = new JSONObject(aiText);
            } catch (Exception e) {
                aiJson = new JSONObject(extractJsonFromText(aiText));
            }

            List<String> improvements = parseToList(aiJson.opt("improvements"));
            List<String> missingSkills = parseToList(aiJson.opt("missingSkills"));
            String motivation = aiJson.optString("motivation", "Keep going, you can do it!");

            return new ImprovementResult(improvements, missingSkills, motivation);
        }
    }

    /**
     * Parse AI field into a clean List<String> regardless of format
     */
    private List<String> parseToList(Object field) {
        if (field == null) return new ArrayList<>();

        if (field instanceof JSONArray arr) {
            List<String> list = new ArrayList<>();
            for (int i = 0; i < arr.length(); i++) {
                list.add(arr.optString(i).trim());
            }
            return list;
        }

        String str = field.toString().trim();

        // Try parsing as JSON array string
        if (str.startsWith("[") && str.endsWith("]")) {
            try {
                JSONArray arr = new JSONArray(str);
                List<String> list = new ArrayList<>();
                for (int i = 0; i < arr.length(); i++) {
                    list.add(arr.optString(i).trim());
                }
                return list;
            } catch (Exception ignored) {}
        }

        // Handle comma-separated values
        if (str.contains(",")) {
            return Arrays.stream(str.split(","))
                    .map(String::trim)
                    .filter(s -> !s.isEmpty())
                    .toList();
        }

        // Fallback: single item list
        return List.of(str);
    }

    private String extractJsonFromText(String text) {
        int start = text.indexOf("{");
        int end = text.lastIndexOf("}");
        if (start != -1 && end != -1 && end > start) {
            return text.substring(start, end + 1);
        }
        throw new RuntimeException("No valid JSON object found in AI response.");
    }
}
