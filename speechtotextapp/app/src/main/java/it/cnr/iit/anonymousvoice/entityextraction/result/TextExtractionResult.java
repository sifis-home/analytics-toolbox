package it.cnr.iit.anonymousvoice.entityextraction.result;

import java.util.List;

import it.cnr.iit.anonymousvoice.entityextraction.result.entityanonymization.EntityAnonymization;

/**
 * This class holds information about entity extraction on a text
 */
public class TextExtractionResult {

    /**
     * The text on which entity extraction has been performed
     */
    private String originalText;
    /**
     * List of extracted entities
     */
    private List<EntityExtractionResult> extractedEntities;

    public TextExtractionResult(String originalText, List<EntityExtractionResult> extractedEntities) {
        this.originalText = originalText;
        this.extractedEntities = extractedEntities;
    }

    public String getOriginalText() {
        return originalText;
    }

    public void setOriginalText(String originalText) {
        this.originalText = originalText;
    }

    public List<EntityExtractionResult> getExtractedEntities() {
        return extractedEntities;
    }

    public void setExtractedEntities(List<EntityExtractionResult> extractedEntities) {
        this.extractedEntities = extractedEntities;
    }

    /**
     * Anonymize the text given a transform function
     * @param anonymization
     * @return
     */
    public String getAnonymizedText(EntityAnonymization anonymization) {
        StringBuffer buffer = new StringBuffer(this.getOriginalText());

        getExtractedEntities().forEach(extracted ->{
            buffer.replace(extracted.getStartPosition(), extracted.getEndPosition(),
                    anonymization.anonymize(extracted.getOriginalEntityText()));

        });

        return buffer.toString();
    }
}
