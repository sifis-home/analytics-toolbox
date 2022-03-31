package it.cnr.iit.anonymousvoice.texttospeech.provider.googletts;

import android.content.Context;
import android.os.Bundle;
import android.os.Environment;
import android.speech.tts.TextToSpeech;
import android.speech.tts.UtteranceProgressListener;
import android.util.Log;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import it.cnr.iit.anonymousvoice.enums.LanguageEnum;
import it.cnr.iit.anonymousvoice.texttospeech.TextToSpeechError;
import it.cnr.iit.anonymousvoice.texttospeech.TextToSpeechLifecycleCallbacks;
import it.cnr.iit.anonymousvoice.texttospeech.TextToSpeechProvider;

/**
 * A text to speech provider based on google library
 */
public class GoogleTTSProvider implements TextToSpeechProvider {

    private static final String TAG = "GoogleTTSProvider";

    private static final String UTTERANCE_ID = "GoogleTextToSpeechRequest";

    private TextToSpeech tts;

    public GoogleTTSProvider(Context context) {
        tts = new TextToSpeech(context, status -> {
            if(status == TextToSpeech.SUCCESS){
                Log.i(TAG, "Text to speech initialize!");
            }
            else{
                Log.e(TAG, "Cannot initialize text to speech provider!");
            }

        });
    }

    @Override
    public void speak(String text, LanguageEnum language, TextToSpeechLifecycleCallbacks callbacks) {
        int res = setLanguage(language);
        if(!isResultCodeOk(res)){
            callbacks.onError(new TextToSpeechError("Found an error while setting Text To Speech language", res));
            return;
        }

        tts.setOnUtteranceProgressListener(new UtteranceProgressListener() {
            @Override
            public void onStart(String utteranceId) {
                Log.i(TAG, "onStart: Starting Process for utteranceId " + utteranceId);
            }

            @Override
            public void onDone(String utteranceId) {
                Log.i(TAG, "onStart: Process done for utteranceId " + utteranceId);
                callbacks.onSuccess();
            }

            @Override
            public void onError(String utteranceId) {
                Log.e(TAG, "onError: Error for utteranceId " + utteranceId);
                callbacks.onError(new TextToSpeechError("Error found when synthesizing text", TextToSpeech.ERROR));
            }
        });

        tts.speak(text,
                //For every new entry, queue's elements are dropped and replaced
                //by new one
                TextToSpeech.QUEUE_FLUSH,
                //Request parameters
                getParameters(),
                //UtteranceId: is a request identifier.
                UTTERANCE_ID);
    }

    @Override
    public void saveSpeakToFile(String text, LanguageEnum language, File destinationFile,
                                TextToSpeechLifecycleCallbacks callbacks) {
        int res = setLanguage(language);
        if(!isResultCodeOk(res)){
            callbacks.onError(new TextToSpeechError("Found an error while setting Text To Speech language", res));
            return;
        }

        if(text.length() > tts.getMaxSpeechInputLength()){
            callbacks.onError(new TextToSpeechError("Speech is longer than Provider's max input speech length!", res));
            return;
        }

        tts.setOnUtteranceProgressListener(new UtteranceProgressListener() {
            @Override
            public void onStart(String utteranceId) {
                Log.i(TAG, "onStart: Saving speech on file...");
            }

            @Override
            public void onDone(String utteranceId) {
                Log.i(TAG, "onDone: Speech file saved!");
                callbacks.onSuccess();
            }

            @Override
            public void onError(String utteranceId) {
                Log.e(TAG, "onError: Error while saving speech file!");
                callbacks.onError(new TextToSpeechError("Error while saving file",
                        TextToSpeech.ERROR));
            }
        });
        tts.synthesizeToFile(text, getParameters(), destinationFile, UTTERANCE_ID);
    }

    private Bundle getParameters() {
        Bundle params = new Bundle();

        params.putString(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID, UTTERANCE_ID);

        return params;
    }

    /**
     * Sets the language locale on TTS engine
     * @param language
     * @return result code
     */
    private int setLanguage(LanguageEnum language) {
        int result = tts.setLanguage(fromEnumToLocale(language));
        if(result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED){
            Log.e(TAG, "setLanguage: Cannot set locale");
        }

        return result;
    }

    /**
     * Returns the Locale from LanguageEnum
     * @param language
     * @return
     */
    private Locale fromEnumToLocale(LanguageEnum language){
        Locale loc = null;
        switch(language){
            case ITALIAN:
                loc = Locale.ITALIAN;
                break;
            case US_ENGLISH:
            default:
                loc = Locale.ENGLISH;
        }
        return  loc;
    }

    /**
     * Checks if the code indicates a successful operation or not
     * @param code
     * @return
     */
    private boolean isResultCodeOk(int code){
        return TextToSpeech.SUCCESS == code;
    }
}
