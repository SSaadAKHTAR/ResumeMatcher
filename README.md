# 📄 Resume Matcher

An **AI-powered Resume Matcher** application that compares candidate resumes against job descriptions and calculates a **match score**.  
Built with **Spring Boot (Java)** for the backend and **React** for the frontend.

---

## 🚀 Features
- Upload **Resume (PDF)** and **Job Description (Text)**  
- Extract candidate **skills** from the resume  
- Compare skills with job requirements using **matching strategies**  
- Generate a **Match Score (%)** with detailed results  
- Display results on a clean, interactive **React UI**  

---

## 🏗️ Architecture

**Backend (Spring Boot, Java)**  
- `Controller` → Handles API requests from frontend  
- `Service` → Business logic layer  
- `Engine` → Core logic to calculate match score  
- `Strategy` → Pluggable matching strategies (e.g., keyword, semantic)  
- `Model` → Data classes (Resume, Job, MatchResult)  
- `Utils` → Helpers for parsing resumes  

**Frontend (React, JavaScript)**  
- File Upload interface (Resume + Job Description)  
- API calls to Spring Boot backend  
- Displays parsed skills and match score with charts  

---

## 📂 Project Structure

```bash
resume-matcher/  
│── backend/ (Spring Boot Java)  
│   ├── src/main/java/com/resumematcher  
│   │   ├── controller  
│   │   ├── service  
│   │   ├── engine  
│   │   ├── strategy  
│   │   ├── model  
│   │   ├── utils  
│   └── ResumeMatcherApplication.java  
│  
│── frontend/ (React JS)  
│   ├── src/  
│   │   ├── components/  
│   │   ├── pages/  
│   │   ├── App.js  
│   │   ├── index.js  
│  
└── README.md
