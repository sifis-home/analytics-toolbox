package it.cnr.iit.anonymousvoice.speechrecognition.provider.deepspeech;

import android.content.res.AssetManager;
import android.util.Log;

import com.mozilla.speechlibrary.utils.ModelUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import it.cnr.iit.anonymousvoice.enums.LanguageEnum;

public class DeepSpeechUtil {

    private static final String TAG = "DeepSpeechUtil";

    private static final String MODEL_FOLDER = "model";

    private static final String JSON_FILENAME = "info.json";
    //private static final String SCORER_FILENAME = "scorer";
    private static final String OUTPUT_FILENAME = "output_graph.tflite";
    private static final String LM_FILENAME = "lm.binary";
    private static final String TRIE_FILENAME = "trie";
    private static final String STORE_SAMPLE_FILENAME = ".keepClips";
    //private static final String NO_DECODER_FILENAME = ".noUseDecoder";

    /**
     * Base directory of app files
     */
    private File appFilesDir;
    /**
     * Asset manager used to find model files
     */
    private AssetManager assetManager;

    public DeepSpeechUtil(File appFilesDir, AssetManager assetManager) {
        this.appFilesDir = appFilesDir;
        this.assetManager = assetManager;
    }

    /**
     * Creates, if does not exist, application model folder for the selected language
     * @param language
     * @return absolute path of the model folder
     */
    public String createApplicationModelFolder(LanguageEnum language) {
        File model = new File(appFilesDir, MODEL_FOLDER);
        if(!model.exists()){
            Log.i(TAG, "createApplicationModelFolder: " + model.getAbsolutePath() + " does not exists. Creating folder...");
            model.mkdir();
        }

        File languageModel = new File(model, language.toString());
        if(!languageModel.exists()){
            Log.i(TAG, "createApplicationModelFolder: language " + languageModel.toString() + " model folder does not exists. Creating sub folder...");
            languageModel.mkdir();
        }

        return languageModel.getAbsolutePath();
    }

    public InputStream getInfoJsonInputStream(LanguageEnum language) throws IOException {
        return assetManager.open( MODEL_FOLDER + "/" + language.toString() + "/" + JSON_FILENAME);
    }

    public OutputStream getInfoJsonOutputStream(LanguageEnum language) throws IOException {
        return new FileOutputStream(getModelFolder(language) + "/" + JSON_FILENAME);
    }

    public InputStream getOutputGraphInputStream(LanguageEnum language) throws IOException {
        return assetManager.open(MODEL_FOLDER + "/"  + language.toString() + "/" + OUTPUT_FILENAME);
    }

    public OutputStream getOutputGraphOutputStream(LanguageEnum language) throws IOException {
        return new FileOutputStream(getModelFolder(language) + "/" + OUTPUT_FILENAME);
    }

    public InputStream getLmBinaryInputStream(LanguageEnum language) throws IOException {
        return assetManager.open(MODEL_FOLDER + "/"  + language.toString() + "/" + LM_FILENAME);
    }

    public OutputStream getLmBinaryOutputStream(LanguageEnum language) throws IOException {
        return new FileOutputStream(getModelFolder(language) + "/" + LM_FILENAME);
    }

    public InputStream getTrieInputStream(LanguageEnum language) throws IOException {
        return assetManager.open(MODEL_FOLDER + "/"  + language.toString() + "/" + TRIE_FILENAME);
    }

    public OutputStream getTrieOutputStream(LanguageEnum language) throws IOException {
        return new FileOutputStream(getModelFolder(language) + "/" + TRIE_FILENAME);
    }

    /**
     * Creates file which tell to androidspeech library to store sample file
     * @param isStoreSampleActive
     * @param language
     */
    public void setStoreSampleFile(boolean isStoreSampleActive, LanguageEnum language) {
        try{
            File storeSampleFile = new File(getModelFolder(language) + "/" + STORE_SAMPLE_FILENAME);
            if (isStoreSampleActive){
                storeSampleFile.createNewFile();
            }else{
                storeSampleFile.delete();
            }
        }catch(IOException ignored){
            Log.e(TAG, "Failed to add or remove store sample file: "
                    + ignored.getMessage(), ignored);
        }
    }

    /**
     * Returns language model folder
     * @param language
     * @return model folder absolute path
     */
    public String getModelFolder(LanguageEnum language){
        File model = new File(appFilesDir, MODEL_FOLDER);
        File languageModel = new File(model, language.toString());
        return languageModel.getAbsolutePath();
    }

    /**
     * Checks if the model is ready
     * @param language
     * @return
     */
    public boolean isModelReady(LanguageEnum language) {
        String model = getModelFolder(language);
        return ModelUtils.isReady(model);
    }

}
