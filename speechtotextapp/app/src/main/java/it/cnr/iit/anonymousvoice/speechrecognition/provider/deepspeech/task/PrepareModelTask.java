package it.cnr.iit.anonymousvoice.speechrecognition.provider.deepspeech.task;

import android.content.res.AssetManager;
import android.util.Log;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.concurrent.Callable;

import it.cnr.iit.anonymousvoice.enums.LanguageEnum;
import it.cnr.iit.anonymousvoice.speechrecognition.provider.deepspeech.DeepSpeechUtil;

/**
 * Task used to prepare the model of deep speech provider.
 * How it works:
 *  a) Model files (info.json, output_graph.tflite, scorer) are stored as application assets
 *  b) When user records for the first time model files are copied from assets to application folder.
 */
class PrepareModelTask implements Callable<PrepareModelResult> {

    private static final String TAG = "PrepareModelTask";

    /**
     * Language of the model
     */
    private LanguageEnum language;
    /**
     * Utility class for retrieving model paths
     */
    private DeepSpeechUtil util;

    PrepareModelTaskRunner.PrepareModelTaskRunnerCallbacks callbacks;

    public PrepareModelTask(LanguageEnum language, File appFilesDir, AssetManager assetManager,
                            PrepareModelTaskRunner.PrepareModelTaskRunnerCallbacks callbacks) {
        this.language = language;
        this.util = new DeepSpeechUtil(appFilesDir, assetManager);
        this.callbacks = callbacks;
    }

    @Override
    public PrepareModelResult call() throws Exception {

        util.createApplicationModelFolder(language);
        copyInfoJson();
        copyOutputGraph();
        copyScorer();

        return null;
    }

    private void copyInfoJson() {
        Log.i(TAG, "copyInfoJson: Copying infoJson file...");

        notify("Copying info.json file...", 0);

        try {
            InputStream infoJson = util.getInfoJsonInputStream(language);
            OutputStream dest = util.getInfoJsonOutputStream(language);

            copy(infoJson, dest, true);

            infoJson.close();
            dest.close();
        }catch (IOException ex){
            Log.e(TAG, "copyInfoJson: Exception caught while reading source file", ex);
        }

        notify("info.json has been copied", 100);

        Log.i(TAG, "copyInfoJson: infoJson file has been copied");
    }

    private void copyOutputGraph() {
        Log.i(TAG, "copyOutputGraph: Copying output_graph.tflite file...");

        notify("Copying output_graph.tflite file...", 0);

        try {
            InputStream outputGraph = util.getOutputGraphInputStream(language);
            OutputStream dest = util.getOutputGraphOutputStream(language);

            copy(outputGraph, dest, true);

            outputGraph.close();
            dest.close();
        }catch (IOException ex){
            Log.e(TAG, "copyOutputGraph: Exception caught while reading source file", ex);
        }

        notify("output_graph.tflite has been copied", 100);

        Log.i(TAG, "copyOutputGraph: output_graph.tflite file has been copied");
    }

    private void copyScorer() {
        Log.i(TAG, "copyScorer: Copying scorer files...");

        notify("Copying lm.binary file...", 0);

        try {
            InputStream lmBinaryInputStream = util.getLmBinaryInputStream(language);
            OutputStream lmBinaryOutputStream = util.getLmBinaryOutputStream(language);

            copy(lmBinaryInputStream, lmBinaryOutputStream, true);

            lmBinaryInputStream.close();
            lmBinaryOutputStream.close();
        }catch (IOException ex){
            Log.e(TAG, "copyScorer: Exception caught while reading source file", ex);
        }

        notify("lm.binary has been copied", 100);

        notify("Copying trie file...", 0);

        try {
            InputStream trieInputStream = util.getTrieInputStream(language);
            OutputStream trieOutputStream = util.getTrieOutputStream(language);

            copy(trieInputStream, trieOutputStream, true);

            trieInputStream.close();
            trieOutputStream.close();
        }catch (IOException ex){
            Log.e(TAG, "copyScorer: Exception caught while reading source file", ex);
        }

        notify("trie has been copied", 100);

        Log.i(TAG, "copyScorer: scorer files has been copied");
    }

    private void copy(InputStream from, OutputStream to, boolean notify) throws IOException {
        long lengthOfFile = from.available();
        byte[] buf = new byte[512];
        int len;
        long total = 0;
        while ((len = from.read(buf)) != -1) {
            total += len;
            if(notify){
                notify("", calculatePercentage(lengthOfFile, total));
            }
            to.write(buf, 0, len);
        }
    }

    private void notify(String message, int progress) {
        this.callbacks.onUpdate(message, progress);
    }

    private int calculatePercentage(long lengthOfFile, long actual){
        return (int)((actual*100)/lengthOfFile);
    }
}
