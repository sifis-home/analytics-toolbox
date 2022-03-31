package it.cnr.iit.anonymousvoice.speechrecognition.provider.deepspeech;

import com.mozilla.speechlibrary.stt.STTResult;

import it.cnr.iit.anonymousvoice.speechrecognition.SpeechToTextResult;

/**
 * Adapter class for speech to text result
 */
public class DeepSpeechResult implements SpeechToTextResult {

    private STTResult result;

    public DeepSpeechResult(STTResult result){
        this.result = result;
    }

    @Override
    public String getTranscription() {
        return result.mTranscription;
    }

    @Override
    public float getConfidence() {
        return result.mConfidence;
    }
}
