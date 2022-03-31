package it.cnr.iit.anonymousvoice.test.deepspeech;

import android.content.Context;
import android.os.Environment;
import android.util.Log;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mozilla.deepspeech.libdeepspeech.DeepSpeechModel;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import it.cnr.iit.anonymousvoice.test.util.FileUtil;

/**
 * This class aims to make easier speech to text test
 * using deepSpeech provider.
 * INSTRUCTION:
 *      I) Copy inside test raw folder model file and corresponding scorer files (trie and lm.binary)
 *          of the language you want to test.
 *          raw folder is placed on app/src/androidTest/res/raw
 *      II) Copy the audio file you want to test, or use the already provided ones. New audio MUST be:
 *          a) Recorder with a single channel
 *          b) Have a 16KHz bitrates
 *      III) Choose the audio file you want to test by changing the selected resource in init function
 */
@RunWith(AndroidJUnit4.class)
public class DeepSpeechUnitTest {

    private static final String TAG = "DeepSpeechUnitTest";

    private static final String MODEL_FILENAME = "output_graph.tflite";
    private static final String LM_FILENAME = "lm.binary";
    private static final String TRIE_FILENAME = "trie";
    /**
     *
     */
    private static final int BEAM_WIDTH = 500;
    private static final float LM_ALPHA = 0.931289039105002f;
    private static final float LM_BETA = 1.1834137581510284f;
    /**
     *
     */
    private static final String TEST_FILENAME = "audio_test.wav";

    private DeepSpeechModel model = null;

    String modelFile = null;
    String lmFile = null;
    String trieFile = null;
    String audioFile = null;

    @Before
    public void init() throws IOException {
        Context context = InstrumentationRegistry.getInstrumentation().getContext();

        InputStream is = null;
        OutputStream os = null;

        modelFile = context.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS) + "/" + MODEL_FILENAME;
        lmFile = context.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS) + "/" + LM_FILENAME;
        trieFile = context.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS) + "/" + TRIE_FILENAME;
        audioFile = context.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS) + "/" + TEST_FILENAME;

        is = FileUtil.getResourceStream(InstrumentationRegistry.getInstrumentation(),
                it.cnr.iit.anonymousvoice.test.R.raw.output_graph);
        os = new FileOutputStream(modelFile);
        FileUtil.copy(is, os);
        is.close();
        os.close();

        is = FileUtil.getResourceStream(InstrumentationRegistry.getInstrumentation(),
                it.cnr.iit.anonymousvoice.test.R.raw.lm);
        os = new FileOutputStream(lmFile);
        FileUtil.copy(is, os);
        is.close();
        os.close();

        is = FileUtil.getResourceStream(InstrumentationRegistry.getInstrumentation(),
                it.cnr.iit.anonymousvoice.test.R.raw.trie);
        os = new FileOutputStream(trieFile);
        FileUtil.copy(is, os);
        is.close();
        os.close();

        /**
         * TODO select here the audio file you want to use
         *      for testing
         */
        is = FileUtil.getResourceStream(InstrumentationRegistry.getInstrumentation(),
                it.cnr.iit.anonymousvoice.test.R.raw.e0004);
        os = new FileOutputStream(audioFile);
        FileUtil.copy(is, os);
        is.close();
        os.close();

        model = new DeepSpeechModel(modelFile, BEAM_WIDTH);
        model.enableDecoderWihLM(lmFile, trieFile, LM_ALPHA, LM_BETA);
    }

    @After
    public void clean(){
        FileUtil.removeFile(modelFile);
        FileUtil.removeFile(lmFile);
        FileUtil.removeFile(trieFile);
        FileUtil.removeFile(audioFile);
    }

    private char readLEChar(RandomAccessFile f) throws IOException {
        byte b1 = f.readByte();
        byte b2 = f.readByte();
        return (char)((b2 << 8) | b1);
    }

    private int readLEInt(RandomAccessFile f) throws IOException {
        byte b1 = f.readByte();
        byte b2 = f.readByte();
        byte b3 = f.readByte();
        byte b4 = f.readByte();
        return (int)((b1 & 0xFF) | (b2 & 0xFF) << 8 | (b3 & 0xFF) << 16 | (b4 & 0xFF) << 24);
    }

    /**
     * Testing inference quality of deepSpeech speech to text provider.
     * @throws IOException
     */
    @Test
    public void deepSpeechInferenceTest() throws IOException {
        long inferenceExecTime = 0;

        try {
            RandomAccessFile wave = new RandomAccessFile(audioFile, "r");

            wave.seek(20); char audioFormat = this.readLEChar(wave);
            assert (audioFormat == 1); // 1 is PCM
            // tv_audioFormat.setText("audioFormat=" + (audioFormat == 1 ? "PCM" : "!PCM"));

            wave.seek(22); char numChannels = this.readLEChar(wave);
            assert (numChannels == 1); // MONO
            // tv_numChannels.setText("numChannels=" + (numChannels == 1 ? "MONO" : "!MONO"));

            wave.seek(24); int sampleRate = this.readLEInt(wave);
            assert (sampleRate == this.model.sampleRate()); // desired sample rate
            // tv_sampleRate.setText("sampleRate=" + (sampleRate == 16000 ? "16kHz" : "!16kHz"));

            wave.seek(34); char bitsPerSample = this.readLEChar(wave);
            assert (bitsPerSample == 16); // 16 bits per sample
            // tv_bitsPerSample.setText("bitsPerSample=" + (bitsPerSample == 16 ? "16-bits" : "!16-bits" ));

            wave.seek(40); int bufferSize = this.readLEInt(wave);
            assert (bufferSize > 0);
            // tv_bufferSize.setText("bufferSize=" + bufferSize);

            wave.seek(44);
            byte[] bytes = new byte[bufferSize];
            wave.readFully(bytes);

            short[] shorts = new short[bytes.length/2];
            // to turn bytes to shorts as either big endian or little endian.
            ByteBuffer.wrap(bytes).order(ByteOrder.LITTLE_ENDIAN).asShortBuffer().get(shorts);

            long inferenceStartTime = System.currentTimeMillis();

            String decoded = this.model.stt(shorts, shorts.length);
            inferenceExecTime = System.currentTimeMillis() - inferenceStartTime;

            Log.i(TAG, "Inference last " + inferenceExecTime);
            Log.i(TAG, "Translated Speech: " + decoded);

        } catch (FileNotFoundException ex) {

        } catch (IOException ex) {

        } finally {

        }
    }

}
