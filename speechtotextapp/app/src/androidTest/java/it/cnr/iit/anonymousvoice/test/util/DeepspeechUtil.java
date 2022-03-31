package it.cnr.iit.anonymousvoice.test.util;

import android.util.Log;

import org.mozilla.deepspeech.libdeepspeech.DeepSpeechModel;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

/**
 * Utility class for deepspeech inference
 */
public class DeepspeechUtil {

    public static char readLEChar(RandomAccessFile f) throws IOException {
        byte b1 = f.readByte();
        byte b2 = f.readByte();
        return (char)((b2 << 8) | b1);
    }

    public static int readLEInt(RandomAccessFile f) throws IOException {
        byte b1 = f.readByte();
        byte b2 = f.readByte();
        byte b3 = f.readByte();
        byte b4 = f.readByte();
        return (int)((b1 & 0xFF) | (b2 & 0xFF) << 8 | (b3 & 0xFF) << 16 | (b4 & 0xFF) << 24);
    }

    /**
     * Given an audio file and a model performs a speech to text process
     * @param fileToTest
     * @param model
     * @return decoded string
     * @throws IOException
     */
    public static String inference(String fileToTest, DeepSpeechModel model) throws IOException{
            RandomAccessFile wave = new RandomAccessFile(fileToTest, "r");

            wave.seek(20); char audioFormat = readLEChar(wave);
            assert (audioFormat == 1); // 1 is PCM
            // tv_audioFormat.setText("audioFormat=" + (audioFormat == 1 ? "PCM" : "!PCM"));

            wave.seek(22); char numChannels = readLEChar(wave);
            assert (numChannels == 1); // MONO
            // tv_numChannels.setText("numChannels=" + (numChannels == 1 ? "MONO" : "!MONO"));

            wave.seek(24); int sampleRate = readLEInt(wave);
            assert (sampleRate == model.sampleRate()); // desired sample rate
            // tv_sampleRate.setText("sampleRate=" + (sampleRate == 16000 ? "16kHz" : "!16kHz"));

            wave.seek(34); char bitsPerSample = readLEChar(wave);
            assert (bitsPerSample == 16); // 16 bits per sample
            // tv_bitsPerSample.setText("bitsPerSample=" + (bitsPerSample == 16 ? "16-bits" : "!16-bits" ));

            wave.seek(40); int bufferSize = readLEInt(wave);
            assert (bufferSize > 0);
            // tv_bufferSize.setText("bufferSize=" + bufferSize);

            wave.seek(44);
            byte[] bytes = new byte[bufferSize];
            wave.readFully(bytes);

            short[] shorts = new short[bytes.length/2];
            // to turn bytes to shorts as either big endian or little endian.
            ByteBuffer.wrap(bytes).order(ByteOrder.LITTLE_ENDIAN).asShortBuffer().get(shorts);

            String decoded = model.stt(shorts, shorts.length);

            return decoded;
    }

}
