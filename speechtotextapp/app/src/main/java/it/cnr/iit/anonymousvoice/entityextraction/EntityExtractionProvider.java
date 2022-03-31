package it.cnr.iit.anonymousvoice.entityextraction;

import java.util.List;

import it.cnr.iit.anonymousvoice.entityextraction.filter.FilterEntity;
import it.cnr.iit.anonymousvoice.enums.LanguageEnum;

/**
 * Interface for entity extractor provider
 */
public interface EntityExtractionProvider {

    /**
     * Processes a text for extracting entities
     * @param text the text on which extraction will be performed
     * @param language the language used for extraction
     * @param filter a list of entity filter. It will be extract only the entities in the list,
     * @param callbacks
     */
    void extract(String text, LanguageEnum language, List<FilterEntity> filter, EntityExtractionCallbacks callbacks);

    /**
     * Returns the list of applicable entity filters to this provider
     * @return
     */
    List<FilterEntity> getHandledEntities();
}
