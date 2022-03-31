package it.cnr.iit.anonymousvoice.speechrecognition;

/**
 * Interface for lifecycle steps of a speech to text provider
 */
public interface SpeechToTextLifecycleCallbacks {

    /**
     * Callback for handling model missing event
     */
    void onModelMissing();

    /**
     * Callback for handling speech recognition error
     * @param errorMessage
     */
    void onError(String errorMessage);

    /**
     * Handle success callback
     * @param speechToTextResult result of the speech to text service
     */
    void onSuccess(SpeechToTextResult speechToTextResult);
}
