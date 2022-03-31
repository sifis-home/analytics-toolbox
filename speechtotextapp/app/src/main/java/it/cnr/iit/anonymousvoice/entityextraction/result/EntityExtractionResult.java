package it.cnr.iit.anonymousvoice.entityextraction.result;

/**
 * Result of a single entity
 */
public class EntityExtractionResult {

    /**
     * Start position of this entity in respect to original text
     */
    private int startPosition;
    /**
     * End position of this entity in respect to original text
     */
    private int endPosition;
    /**
     * original text of the entity
     */
    private String originalEntityText;
    /**
     * formatted text of the entity (e.g. a formatted phone number)
     */
    private String formattedText;

    public EntityExtractionResult(int startPosition, int endPosition, String originalEntityText,
                                  String formattedText) {
        this.startPosition = startPosition;
        this.endPosition = endPosition;
        this.originalEntityText = originalEntityText;
        this.formattedText = formattedText;
    }

    public int getStartPosition() {
        return startPosition;
    }

    public void setStartPosition(int startPosition) {
        this.startPosition = startPosition;
    }

    public int getEndPosition() {
        return endPosition;
    }

    public void setEndPosition(int endPosition) {
        this.endPosition = endPosition;
    }

    public String getOriginalEntityText() {
        return originalEntityText;
    }

    public void setOriginalEntityText(String originalEntityText) {
        this.originalEntityText = originalEntityText;
    }

    public String getFormattedText() {
        return formattedText;
    }

    public void setFormattedText(String formattedText) {
        this.formattedText = formattedText;
    }
}
