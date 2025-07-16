package com.example.resumematcher.nlp;

import edu.stanford.nlp.pipeline.*;
import edu.stanford.nlp.ling.CoreAnnotations;
import edu.stanford.nlp.ling.CoreLabel;

import java.util.*;
import java.util.stream.Collectors;

public class ResumeNLPProcessor {

    private StanfordCoreNLP pipeline;

    public ResumeNLPProcessor() {
        // Setup CoreNLP pipeline with required annotators
        Properties props = new Properties();
        props.setProperty("annotators", "tokenize,ssplit,pos,lemma,ner");
        this.pipeline = new StanfordCoreNLP(props);
    }

    public List<String> extractKeywords(String resumeText) {
        List<String> keywords = new ArrayList<>();

        CoreDocument doc = new CoreDocument(resumeText);
        pipeline.annotate(doc);

        // Basic keyword logic: collect all nouns
        for (CoreLabel tok : doc.tokens()) {
            String pos = tok.get(CoreAnnotations.PartOfSpeechAnnotation.class);
            if (pos.startsWith("NN")) {
                keywords.add(tok.word().toLowerCase());
            }
        }

        // Optionally: remove duplicates and stopwords
        return keywords.stream()
                .distinct()
                .filter(w -> w.length() > 2)
                .collect(Collectors.toList());
    }
}
