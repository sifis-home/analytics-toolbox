package it.cnr.iit.anonymousvoice.test.integration;

import it.cnr.iit.anonymousvoice.entityextraction.result.TextExtractionResult;

public class TestResult {

    private String reference;
    private String hypothesis;
    private float wordErrorRate;
    private TextExtractionResult extractionResult;

    public String getReference() {
        return reference;
    }

    public void setReference(String reference) {
        this.reference = reference;
    }

    public String getHypothesis() {
        return hypothesis;
    }

    public void setHypothesis(String hypothesis) {
        this.hypothesis = hypothesis;
    }

    public float getWordErrorRate() {
        return wordErrorRate;
    }

    public void setWordErrorRate(float wordErrorRate) {
        this.wordErrorRate = wordErrorRate;
    }

    public TextExtractionResult getExtractionResult() {
        return extractionResult;
    }

    public void setExtractionResult(TextExtractionResult extractionResult) {
        this.extractionResult = extractionResult;
    }
}
