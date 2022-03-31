package it.cnr.iit.anonymousvoice.entityextraction.provider.googlemlkit;

import android.telephony.PhoneNumberUtils;

import com.google.mlkit.nl.entityextraction.DateTimeEntity;
import com.google.mlkit.nl.entityextraction.Entity;
import com.google.mlkit.nl.entityextraction.EntityAnnotation;
import com.google.mlkit.nl.entityextraction.FlightNumberEntity;
import com.google.mlkit.nl.entityextraction.IbanEntity;
import com.google.mlkit.nl.entityextraction.IsbnEntity;
import com.google.mlkit.nl.entityextraction.MoneyEntity;
import com.google.mlkit.nl.entityextraction.PaymentCardEntity;
import com.google.mlkit.nl.entityextraction.TrackingNumberEntity;

import java.text.SimpleDateFormat;
import java.util.Date;

import it.cnr.iit.anonymousvoice.entityextraction.result.EntityExtractionResult;

public class MlKitEntityExtractionUtil {

    private static final String DATE_TIME_PATTERN = "yyyy-MM-dd HH:mm:ss";

    /**
     * Transform an entityAnnotation into a EntityResult
     * @param entityAnnotation
     * @return
     */
    public static EntityExtractionResult fromAnnotationToEntityResult(EntityAnnotation entityAnnotation){
        //TODO: for each annotations mlKit could find more than one entity
        // For now I pick the first one!
        String entityOriginalText = entityAnnotation.getAnnotatedText();
        int start = entityAnnotation.getStart();
        // annotation end index is the first character not belonging to entity
        int end = entityAnnotation.getEnd() - 1;

        Entity entity = entityAnnotation.getEntities().get(0);
        String formattedText = getEntityFormattedText(entity, entityOriginalText);

        return new EntityExtractionResult(
                start,
                end,
                entityOriginalText,
                formattedText
        );
    }

    public static String getEntityFormattedText(Entity entity, String annotatedText) {
        int type = entity.getType();

        String formattedText = null;

        switch (type){
            case Entity.TYPE_DATE_TIME:
                formattedText = getDatetimeFormattedText(entity.asDateTimeEntity());
                break;
            case Entity.TYPE_FLIGHT_NUMBER:
                formattedText = getFlightNumberFormattedText(entity.asFlightNumberEntity());
                break;
            case Entity.TYPE_IBAN:
                formattedText = getIbanFormattedText(entity.asIbanEntity());
                break;
            case Entity.TYPE_ISBN:
                formattedText = getIsbnFormattedText(entity.asIsbnEntity());
                break;
            case Entity.TYPE_PAYMENT_CARD:
                formattedText = getPaymentCardFormattedText(entity.asPaymentCardEntity());
                break;
            case Entity.TYPE_PHONE:
                formattedText = getPhoneFormattedText(annotatedText);
                break;
            case Entity.TYPE_TRACKING_NUMBER:
                formattedText = getTrackingNumberFormattedText(entity.asTrackingNumberEntity());
                break;
            case Entity.TYPE_MONEY:
                formattedText = getMoneyFormattedText(entity.asMoneyEntity());
                break;
            default:
                formattedText = annotatedText;
        }

        return formattedText;
    }

    private static String getDatetimeFormattedText(DateTimeEntity entity) {
        SimpleDateFormat dtFormat = new SimpleDateFormat(DATE_TIME_PATTERN);
        return dtFormat.format(new Date(entity.getTimestampMillis()));
    }

    private static String getFlightNumberFormattedText(FlightNumberEntity entity) {
        return entity.getAirlineCode() + "-" + entity.getFlightNumber();
    }

    private static String getIbanFormattedText(IbanEntity entity) {
        return "[" + entity.getIbanCountryCode() + "] " + entity.getIban();
    }

    private static String getIsbnFormattedText(IsbnEntity entity) {
        return entity.getIsbn();
    }

    private static String getPaymentCardFormattedText(PaymentCardEntity entity) {
        return "[" + entity.getPaymentCardNetwork() + "] " + entity.getPaymentCardNumber();
    }

    private static String getPhoneFormattedText(String annotatedText) {
        return PhoneNumberUtils.formatNumber(annotatedText);
    }

    private static String getTrackingNumberFormattedText(TrackingNumberEntity entity) {
        return "[" + entity.getParcelCarrier() + "] " + entity.getParcelTrackingNumber();
    }

    private static String getMoneyFormattedText(MoneyEntity entity) {
        return entity.getUnnormalizedCurrency();
    }

}
