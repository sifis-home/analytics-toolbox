package it.cnr.iit.anonymousvoice.texttospeech;

/**
 * Lifecycle callbacks belonging yo text to speech process
 */
public interface TextToSpeechLifecycleCallbacks {

    /**
     * Called when the process successfully terminate
     */
    void onSuccess();

    /**
     * Called when text to speech process
     * found an error
     * @param error encountered error
     */
    void onError(TextToSpeechError error);

}
