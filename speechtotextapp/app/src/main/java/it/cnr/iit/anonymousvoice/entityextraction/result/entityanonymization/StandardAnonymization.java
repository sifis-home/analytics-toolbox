package it.cnr.iit.anonymousvoice.entityextraction.result.entityanonymization;

/**
 * Replace entity text with a series of "*"
 */
public class StandardAnonymization implements EntityAnonymization{
    @Override
    public String anonymize(String entityText) {
        String result = "";

        for(int i = 0; i < entityText.length() - 1; i++){
            result += "*";
        }

        return result;
    }
}
