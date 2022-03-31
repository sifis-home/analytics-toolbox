package it.cnr.iit.anonymousvoice.entityextraction.provider.googlemlkit;

import android.util.ArraySet;
import android.util.Log;

import com.google.mlkit.nl.entityextraction.Entity;
import com.google.mlkit.nl.entityextraction.EntityExtraction;
import com.google.mlkit.nl.entityextraction.EntityExtractionParams;
import com.google.mlkit.nl.entityextraction.EntityExtractor;
import com.google.mlkit.nl.entityextraction.EntityExtractorOptions;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import it.cnr.iit.anonymousvoice.entityextraction.EntityExtractionCallbacks;
import it.cnr.iit.anonymousvoice.entityextraction.EntityExtractionProvider;
import it.cnr.iit.anonymousvoice.entityextraction.filter.FilterEntity;
import it.cnr.iit.anonymousvoice.entityextraction.result.TextExtractionResult;
import it.cnr.iit.anonymousvoice.entityextraction.result.EntityExtractionResult;
import it.cnr.iit.anonymousvoice.enums.LanguageEnum;

/**
 * An entity extraction implementation based on Google's machine learning kit
 * @see {https://developers.google.com/ml-kit/language/entity-extraction}
 */
public class MlKitEntityExtractionProvider implements EntityExtractionProvider {

    private static final String TAG = "MlKitEntityExtractionProvider";

    @Override
    public List<FilterEntity> getHandledEntities() {
        List<FilterEntity> filters = new ArrayList();

        filters.add(new FilterEntity(Entity.TYPE_ADDRESS, "Address"));
        filters.add(new FilterEntity(Entity.TYPE_DATE_TIME, "Date and time"));
        filters.add(new FilterEntity(Entity.TYPE_EMAIL, "Email address"));
        filters.add(new FilterEntity(Entity.TYPE_FLIGHT_NUMBER, "Flight number"));
        filters.add(new FilterEntity(Entity.TYPE_IBAN, "IBAN"));
        filters.add(new FilterEntity(Entity.TYPE_ISBN, "ISBN"));
        filters.add(new FilterEntity(Entity.TYPE_PAYMENT_CARD, "Payment card"));
        filters.add(new FilterEntity(Entity.TYPE_PHONE, "Phone number"));
        filters.add(new FilterEntity(Entity.TYPE_TRACKING_NUMBER, "tracking number"));
        filters.add(new FilterEntity(Entity.TYPE_URL, "URL"));
        filters.add(new FilterEntity(Entity.TYPE_MONEY, "Money amount"));

        return filters;
    }

    @Override
    public void extract(String text, LanguageEnum language, List<FilterEntity> filter, EntityExtractionCallbacks callbacks) {
        Log.i(TAG, "Extracting entities for text: " + text);

        if(text == null || text.isEmpty()){
            Log.e(TAG, "Text is empty");
            callbacks.onExtractionError("Cannot start extraction process. Text is empty!");
            return;
        }
        if(filter.isEmpty()){
            Log.e(TAG, "Filter is empty");
            callbacks.onExtractionError("Cannot start extraction process. Filter list is empty!");
            return;
        }

        //Creating extractor
        EntityExtractor extractor = EntityExtraction.getClient(
                new EntityExtractorOptions.Builder(getLanguageOption(language))
                        .build());

        //TODO: actually the download happens without conditions.
        // Maybe it would be better if it happens only under certain conditions (e.g. use of wifi)
        extractor
                .downloadModelIfNeeded()
                .addOnSuccessListener((entityAnnotations) -> {
                    Log.i(TAG, "Model download ok!");
                    internalExtract(extractor, text, filter, callbacks);
                })
                .addOnFailureListener((exception) -> {
                    Log.e(TAG, "Model download has given an error", exception);
                    callbacks.onExtractionError(exception.getMessage());
                });
    }

    private void internalExtract(EntityExtractor extractor, String text, List<FilterEntity> filter,
                                 EntityExtractionCallbacks callbacks){
        extractor
                .annotate(getExtractionParams(text, filter))
                .addOnSuccessListener((entityAnnotations) -> {
                    Log.i(TAG, "Found " + entityAnnotations.size() + " annotations");
                    List<EntityExtractionResult> entitiesFound = new ArrayList();
                    entityAnnotations.forEach((annotation) -> {
                        entitiesFound.add(
                                MlKitEntityExtractionUtil.fromAnnotationToEntityResult(annotation)
                        );
                    });

                    TextExtractionResult extractionResult = new TextExtractionResult(text, entitiesFound);
                    callbacks.onExtractionSuccess(extractionResult);
                })
                .addOnFailureListener((exception) -> {
                    Log.e(TAG, "Error in entity extraction process", exception);
                   callbacks.onExtractionError(exception.getMessage());
                });
    }

    private EntityExtractionParams getExtractionParams(String text, List<FilterEntity> filter) {
        EntityExtractionParams params = new EntityExtractionParams
                .Builder(text)
                .setEntityTypesFilter(createListOfFilteredEntities(filter))
                .build();

        return params;
    }

    private Set<Integer> createListOfFilteredEntities(List<FilterEntity> filter){
        Set<Integer> entityIds = new ArraySet();

        filter.forEach(entity -> {
            entityIds.add(entity.getId());
        });

        return entityIds;
    }

    private String getLanguageOption(LanguageEnum language){
        Log.i(TAG, "Creating options for language: " + language);

        String modelLanguage = "";

        switch (language){
            case ITALIAN:
                Log.i(TAG, "Will be use italian model");
                modelLanguage = EntityExtractorOptions.ITALIAN;
                break;
            case US_ENGLISH:
            default:
                Log.i(TAG, "Will be use english model");
                modelLanguage = EntityExtractorOptions.ENGLISH;
        }

        return modelLanguage;
    }
}
