package it.cnr.iit.anonymousvoice.texttospeech;

import java.io.File;

import it.cnr.iit.anonymousvoice.enums.LanguageEnum;

/**
 * Provider interface for text to speech service
 */
public interface TextToSpeechProvider {

    /**
     * Speaks the input text
     * @param text
     * @param language
     * @param callbacks
     */
    void speak(String text, LanguageEnum language, TextToSpeechLifecycleCallbacks callbacks);

    /**
     * Save a file with a speech of the input text
     * @param text
     * @param language
     * @param destinationFile
     * @param callbacks
     */
    void saveSpeakToFile(String text, LanguageEnum language, File destinationFile, TextToSpeechLifecycleCallbacks callbacks);
}
