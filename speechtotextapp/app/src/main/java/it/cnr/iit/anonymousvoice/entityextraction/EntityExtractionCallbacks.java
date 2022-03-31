package it.cnr.iit.anonymousvoice.entityextraction;

import it.cnr.iit.anonymousvoice.entityextraction.result.TextExtractionResult;

public interface EntityExtractionCallbacks {

    /**
     * Success callback for entity extraction
     * @param result of the extraction process
     */
    void onExtractionSuccess(TextExtractionResult result);

    /**
     * Error callback for entity extraction
     * @param errorMessage
     */
    void onExtractionError(String errorMessage);

}
