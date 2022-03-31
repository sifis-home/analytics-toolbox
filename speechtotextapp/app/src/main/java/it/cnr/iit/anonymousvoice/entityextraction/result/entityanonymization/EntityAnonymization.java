package it.cnr.iit.anonymousvoice.entityextraction.result.entityanonymization;

public interface EntityAnonymization {

    /**
     * Anonymize an entity text
     * @param entityText
     * @return
     */
    public String anonymize(String entityText);

}
