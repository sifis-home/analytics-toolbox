package it.cnr.iit.anonymousvoice.test.entityextraction;

import android.util.Log;

import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.google.mlkit.nl.entityextraction.Entity;

import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import it.cnr.iit.anonymousvoice.entityextraction.EntityExtractionCallbacks;
import it.cnr.iit.anonymousvoice.entityextraction.EntityExtractionProvider;
import it.cnr.iit.anonymousvoice.entityextraction.filter.FilterEntity;
import it.cnr.iit.anonymousvoice.entityextraction.provider.googlemlkit.MlKitEntityExtractionProvider;
import it.cnr.iit.anonymousvoice.entityextraction.result.TextExtractionResult;
import it.cnr.iit.anonymousvoice.enums.LanguageEnum;

@RunWith(AndroidJUnit4.class)
public class MlKitEntityExtractionProviderTest {

    private static final String TAG = "MlKitEntityExtractionProviderTest";

    private LanguageEnum language = LanguageEnum.US_ENGLISH;

    private EntityExtractionProvider provider = new MlKitEntityExtractionProvider();

    @Test
    public void testEntityExtraction_1() throws InterruptedException {
        final String text       = "Meet me at 1600 Amphitheatre Parkway, Mountain View, CA, 94043 Let’s organize a meeting to discuss.";
        List<FilterEntity> filers    = Arrays.asList(
                new FilterEntity(Entity.TYPE_ADDRESS, "Address"),
                new FilterEntity(Entity.TYPE_EMAIL, "Email"));

        provider.extract(text, language, filers, new EntityExtractionCallbacks() {
            @Override
            public void onExtractionSuccess(TextExtractionResult result) {
                Assert.assertFalse(result.getExtractedEntities().isEmpty());
                Assert.assertEquals(1, result.getExtractedEntities().size());
                Assert.assertEquals("1600 Amphitheatre Parkway, Mountain View, CA, 94043",
                        result.getExtractedEntities().get(0).getOriginalEntityText());
            }

            @Override
            public void onExtractionError(String errorMessage) {
                Log.e(TAG, "onExtractionError: " + errorMessage );
            }
        });

        Thread.sleep(2500);
    }

    @Test
    public void testEntityExtraction_2() throws InterruptedException {
        final String text       = "Meet me at 1600 Amphitheatre Parkway, Mountain View, CA, 94043 Let’s organize a meeting to discuss.";
        List<FilterEntity> filers    = Arrays.asList(new FilterEntity(Entity.TYPE_EMAIL, "Email"));

        provider.extract(text, language, filers, new EntityExtractionCallbacks() {
            @Override
            public void onExtractionSuccess(TextExtractionResult result) {
                Assert.assertTrue(result.getExtractedEntities().isEmpty());
            }

            @Override
            public void onExtractionError(String errorMessage) {
                Log.e(TAG, "onExtractionError: " + errorMessage );
            }
        });

        Thread.sleep(2500);
    }

    @Test
    public void testEntityExtraction_3() throws InterruptedException{
        final String text = "You can contact the test team tomorrow at info@google.com to determine the best timeline.";
        List<FilterEntity> filers = Arrays.asList(
                new FilterEntity(Entity.TYPE_EMAIL, "Email"),
                new FilterEntity(Entity.TYPE_ADDRESS, "Address"),
                new FilterEntity(Entity.TYPE_DATE_TIME, "DateTime"));

        provider.extract(text, language, filers, new EntityExtractionCallbacks() {
            @Override
            public void onExtractionSuccess(TextExtractionResult result) {
                Assert.assertFalse(result.getExtractedEntities().isEmpty());
                Assert.assertEquals(2, result.getExtractedEntities().size());
            }

            @Override
            public void onExtractionError(String errorMessage) {
                Log.e(TAG, "onExtractionError: " + errorMessage );
            }
        });

        Thread.sleep(2500);
    }

    @Test
    public void testEntityExtraction_4() throws InterruptedException{
        final String text = "I live at 20 Giuseppe rossi street in Rome.";
        List<FilterEntity> filers = Arrays.asList(
                new FilterEntity(Entity.TYPE_ADDRESS, "Address"),
                new FilterEntity(Entity.TYPE_DATE_TIME, "DateTime"),
                new FilterEntity(Entity.TYPE_EMAIL, "Email"),
                new FilterEntity(Entity.TYPE_FLIGHT_NUMBER, "Flight number"),
                new FilterEntity(Entity.TYPE_IBAN, "IBAN"),
                new FilterEntity(Entity.TYPE_ISBN, "ISBN"),
                new FilterEntity(Entity.TYPE_PAYMENT_CARD, "Payment card"),
                new FilterEntity(Entity.TYPE_PHONE, "Phone"),
                new FilterEntity(Entity.TYPE_TRACKING_NUMBER, "tracking number"),
                new FilterEntity(Entity.TYPE_URL, "URL"),
                new FilterEntity(Entity.TYPE_MONEY, "Money"));

        provider.extract(text, language, filers, new EntityExtractionCallbacks() {
            @Override
            public void onExtractionSuccess(TextExtractionResult result) {
                Assert.assertFalse(result.getExtractedEntities().isEmpty());
                Assert.assertEquals(2, result.getExtractedEntities().size());
            }

            @Override
            public void onExtractionError(String errorMessage) {
                Log.e(TAG, "onExtractionError: " + errorMessage );
            }
        });

        Thread.sleep(2500);
    }

    @Test
    public void testEntityExtraction_5() throws InterruptedException{
        final String text = "I have paid 6 dollars for a coffee.";
        List<FilterEntity> filers = Arrays.asList(
                new FilterEntity(Entity.TYPE_ADDRESS, "Address"),
                new FilterEntity(Entity.TYPE_DATE_TIME, "DateTime"),
                new FilterEntity(Entity.TYPE_EMAIL, "Email"),
                new FilterEntity(Entity.TYPE_FLIGHT_NUMBER, "Flight number"),
                new FilterEntity(Entity.TYPE_IBAN, "IBAN"),
                new FilterEntity(Entity.TYPE_ISBN, "ISBN"),
                new FilterEntity(Entity.TYPE_PAYMENT_CARD, "Payment card"),
                new FilterEntity(Entity.TYPE_PHONE, "Phone"),
                new FilterEntity(Entity.TYPE_TRACKING_NUMBER, "tracking number"),
                new FilterEntity(Entity.TYPE_URL, "URL"),
                new FilterEntity(Entity.TYPE_MONEY, "Money"));

        provider.extract(text, language, filers, new EntityExtractionCallbacks() {
            @Override
            public void onExtractionSuccess(TextExtractionResult result) {
                Assert.assertFalse(result.getExtractedEntities().isEmpty());
                Assert.assertEquals(1, result.getExtractedEntities().size());
            }

            @Override
            public void onExtractionError(String errorMessage) {
                Log.e(TAG, "onExtractionError: " + errorMessage );
            }
        });

        Thread.sleep(2500);
    }

    @Test
    public void experimentExtractionTest() throws InterruptedException {
        List<FilterEntity> filers = Arrays.asList(
                new FilterEntity(Entity.TYPE_ADDRESS, "Address"),
                new FilterEntity(Entity.TYPE_DATE_TIME, "DateTime"),
                new FilterEntity(Entity.TYPE_EMAIL, "Email"),
                new FilterEntity(Entity.TYPE_FLIGHT_NUMBER, "Flight number"),
                new FilterEntity(Entity.TYPE_IBAN, "IBAN"),
                new FilterEntity(Entity.TYPE_ISBN, "ISBN"),
                new FilterEntity(Entity.TYPE_PAYMENT_CARD, "Payment card"),
                new FilterEntity(Entity.TYPE_PHONE, "Phone"),
                new FilterEntity(Entity.TYPE_TRACKING_NUMBER, "tracking number"),
                new FilterEntity(Entity.TYPE_URL, "URL"),
                new FilterEntity(Entity.TYPE_MONEY, "Money"));

        List<String> recordSet = Arrays.asList(
            "call me tomorrow",
            "the pen is on the table",
            "it rained yesterday",
            "i have paid six dollars for a coffee",
            "i live in italy"
        );

        final int records = recordSet.size();
        final int[] processed = {0};

        List<TextExtractionResult> extracted = new ArrayList<>();

        recordSet.forEach(r -> {
            provider.extract(r, language, filers, new EntityExtractionCallbacks() {
                @Override
                public void onExtractionSuccess(TextExtractionResult result) {
                    extracted.add(result);
                    processed[0]++;
                }

                @Override
                public void onExtractionError(String errorMessage) {
                    Log.e(TAG, "onExtractionError: " + errorMessage );
                }
            });
        });

        while(processed[0] < records){
            Thread.sleep(500);
        }

        int i = 0;
    }

}
