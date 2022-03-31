package it.cnr.iit.anonymousvoice.speechrecognition;

/**
 * Interface for speech to text success result
 */
public interface SpeechToTextResult {

    /**
     *
     * @return The string of the recognized speech
     */
    String getTranscription();

    /**
     *
     * @return The confidence level of the transcription
     */
    float getConfidence();

}
