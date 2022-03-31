package it.cnr.iit.anonymousvoice.speechrecognition;

/**
 * Interface for speech to text service.
 * The interface aims to abstract the speech to text process from
 * its implementation.
 */
public interface SpeechToTextProvider {

    /**
     * Start the speech to text process
     *
     * @param callbacks SpeechToText lyfecycle callbacks
     */
    void start(SpeechToTextLifecycleCallbacks callbacks);

    /**
     * Stop service
     */
    void stop();

}
