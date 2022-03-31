package it.cnr.iit.anonymousvoice.test.integration;

/**
 * Keeps information about a test record
 */
public class TestRecord {

    private int resourceId;
    private String reference;
    private int numberOfEntities;
    private String filename;

    public TestRecord(int resourceId, String reference, int numberOfEntities, String filename){
        this.resourceId = resourceId;
        this.reference = reference;
        this.numberOfEntities = numberOfEntities;
        this.filename = filename;
    }

    public int getResourceId() {
        return resourceId;
    }

    public void setResourceId(int resourceId) {
        this.resourceId = resourceId;
    }

    public String getReference() {
        return reference;
    }

    public void setReference(String reference) {
        this.reference = reference;
    }

    public int getNumberOfEntities() {
        return numberOfEntities;
    }

    public void setNumberOfEntities(int numberOfEntities) {
        this.numberOfEntities = numberOfEntities;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    public String getFilename() {
        return filename;
    }
}
