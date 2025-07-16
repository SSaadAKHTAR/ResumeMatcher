package com.example.resumematcher.utils;

import org.apache.tika.exception.TikaException;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.parser.AutoDetectParser;
import org.apache.tika.sax.BodyContentHandler;
import org.springframework.web.multipart.MultipartFile;
import org.xml.sax.SAXException;

import java.io.*;

public class PDFResumeParser {

    // ✅ For Spring Boot controller (web upload)
    public String extractText(MultipartFile file) throws IOException, TikaException, SAXException {
        try (InputStream stream = file.getInputStream()) {
            return parseWithTika(stream);
        }
    }

    // ✅ For local testing (CLI file path)
    public String extractText(String filePath) throws IOException, TikaException, SAXException {
        try (InputStream stream = new FileInputStream(new File(filePath))) {
            return parseWithTika(stream);
        }
    }

    private String parseWithTika(InputStream stream) throws IOException, TikaException, SAXException {
        BodyContentHandler handler = new BodyContentHandler(-1);
        Metadata metadata = new Metadata();
        AutoDetectParser parser = new AutoDetectParser();
        parser.parse(stream, handler, metadata);
        return handler.toString();
    }
}
