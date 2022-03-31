package it.cnr.iit.anonymousvoice.speechrecognition.provider.deepspeech;

import android.content.Context;
import android.os.Environment;
import android.util.Log;

import androidx.annotation.Nullable;

import com.mozilla.speechlibrary.SpeechResultCallback;
import com.mozilla.speechlibrary.SpeechService;
import com.mozilla.speechlibrary.SpeechServiceSettings;
import com.mozilla.speechlibrary.stt.STTResult;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import it.cnr.iit.anonymousvoice.enums.LanguageEnum;
import it.cnr.iit.anonymousvoice.speechrecognition.SpeechToTextLifecycleCallbacks;
import it.cnr.iit.anonymousvoice.speechrecognition.SpeechToTextProvider;

/**
 * Implementation of speech to text service based on mozilla deepspeech
 * @see {https://github.com/mozilla/DeepSpeech/releases/tag/v0.9.3}
 */
public class DeepSpeechProvider implements SpeechToTextProvider {

    private static final String TAG = "DeepSpeechService";

    private static final String PRODUCT_TAG = "anonymousVoice";

    private SpeechServiceSettings.Builder builder;

    private SpeechService service;

    private DeepSpeechUtil speechUtil;

    private LanguageEnum language;

    private boolean storeSample;

    private File storeSampleFileDirectory;

    public DeepSpeechProvider(Context applicationContext, LanguageEnum language, boolean storeSample, boolean storeTranscriptions) {
        this.speechUtil = new DeepSpeechUtil(applicationContext.getFilesDir(), applicationContext.getAssets());
        this.builder = new SpeechServiceSettings.Builder()
                .withLanguage(language.toString())
                .withProductTag(PRODUCT_TAG)
                .withModelPath(speechUtil.getModelFolder(language))
                .withStoreSamples(storeSample)
                .withUseDeepSpeech(true)
                .withStoreTranscriptions(storeTranscriptions);
        this.service = new SpeechService(applicationContext);
        this.language = language;
        this.storeSample = storeSample;
        this.storeSampleFileDirectory = applicationContext.getExternalFilesDir(
                Environment.DIRECTORY_DOWNLOADS);
    }

    @Override
    public void start(SpeechToTextLifecycleCallbacks callbacks) {

        //Before start recording we must be sure that the model is loaded
        if(speechUtil.isModelReady(language)){
            final SpeechResultCallback callback = new SpeechResultCallback() {
                @Override
                public void onStartListen() {
                    Log.d(TAG, "onStartListen: Start voice recording...");
                }

                @Override
                public void onMicActivity(double fftsum) {
                    Log.d(TAG, "onMicActivity: Mic activity detected [" + fftsum + "]");
                }

                @Override
                public void onDecoding() {
                    Log.d(TAG, "onDecoding: Decoding recorder voice...");
                }

                @Override
                public void onSTTResult(@Nullable STTResult result) {
                    if(storeSample){
                        moveSampleFile();
                    }

                    Log.d(TAG, "onSTTResult: translation confidence is " + String.valueOf(result.mConfidence));
                    callbacks.onSuccess(new DeepSpeechResult(result));
                }

                @Override
                public void onNoVoice() {
                    Log.d(TAG, "onNoVoice: No voice detected...");
                }

                @Override
                public void onError(int errorType, @Nullable String error) {
                    Log.d(TAG, "onError: [" + errorType + "] - " + error);
                    String errorText = "";
                    switch (errorType){
                        case SPEECH_ERROR:
                            errorText = error;
                            break;
                        case MODEL_NOT_FOUND:
                            errorText = "Model not found. Please verify your configuration.";
                            break;
                        default:

                    }

                    callbacks.onError(errorText);
                }
            };

            speechUtil.setStoreSampleFile(this.storeSample, this.language);

            service.start(builder.build(), callback);
        }else{
            //Loading the model from app assets files
            callbacks.onModelMissing();
        }
    }

    @Override
    public void stop() {
        service.stop();
    }

    /**
     * Moves sample file from app folder to Downloads folder
     */
    private void moveSampleFile() {
        try {
            Log.i(TAG, "moveSampleFile: Moving sample file to download folder");
            
            InputStream is = new FileInputStream(speechUtil.getModelFolder(language) + "/clip_1.wav");
            OutputStream os = new FileOutputStream( this.storeSampleFileDirectory + "/clip_1.wav");

            byte[] buf = new byte[512];
            int len;
            while ((len = is.read(buf)) != -1) {
                os.write(buf, 0, len);
            }
        } catch (IOException e) {
            Log.e(TAG, "onSTTResult: Cannot move sample file: " + e.getMessage(), e);
        }
    }
}
