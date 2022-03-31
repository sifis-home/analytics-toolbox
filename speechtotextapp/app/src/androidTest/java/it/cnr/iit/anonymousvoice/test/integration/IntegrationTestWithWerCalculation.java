package it.cnr.iit.anonymousvoice.test.integration;

import android.os.Environment;
import android.util.Log;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mozilla.deepspeech.libdeepspeech.DeepSpeechModel;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import it.cnr.iit.anonymousvoice.entityextraction.EntityExtractionCallbacks;
import it.cnr.iit.anonymousvoice.entityextraction.EntityExtractionProvider;
import it.cnr.iit.anonymousvoice.entityextraction.filter.FilterEntity;
import it.cnr.iit.anonymousvoice.entityextraction.provider.googlemlkit.MlKitEntityExtractionProvider;
import it.cnr.iit.anonymousvoice.entityextraction.result.EntityExtractionResult;
import it.cnr.iit.anonymousvoice.entityextraction.result.TextExtractionResult;
import it.cnr.iit.anonymousvoice.enums.LanguageEnum;
import it.cnr.iit.anonymousvoice.test.util.DeepspeechUtil;
import it.cnr.iit.anonymousvoice.test.util.FileUtil;
import it.cnr.iit.anonymousvoice.util.WordSequenceAligner;

/**
 * Integration test with word error rate calculation
 * and number of recognized entities to test model accuracy
 */
@RunWith(AndroidJUnit4.class)
public class IntegrationTestWithWerCalculation {

    private static final String TAG = "IntegrationTestWithWerCalculation";

    private static final String DIRECTORY_SEPARATOR = "/";

    private static final String MODEL_FILENAME = "output_graph.tflite";
    private static final String LM_FILENAME = "lm.binary";
    private static final String TRIE_FILENAME = "trie";

    private static final String RESULT_FILENAME = "integration-test.csv";

    /**
     *
     */
    private static final int BEAM_WIDTH = 500;
    private static final float LM_ALPHA = 0.931289039105002f;
    private static final float LM_BETA = 1.1834137581510284f;

    private String basePath;

    private String modelFile = null;
    private String lmFile = null;
    private String trieFile = null;
    private DeepSpeechModel model;

    private LanguageEnum language = LanguageEnum.US_ENGLISH;
    private EntityExtractionProvider entityExtractionProvider;
    private List<FilterEntity> filterEntities;

    private List<TestRecord> filesToTest;

    @Before
    public void init() throws IOException {
        initDeepspeechModel();
        initExtractionProvider();
        initTestFiles();
    }

    private void initDeepspeechModel(){
        basePath = InstrumentationRegistry
                .getInstrumentation()
                .getContext()
                .getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS) + DIRECTORY_SEPARATOR;

        modelFile = basePath + MODEL_FILENAME;
        lmFile = basePath + LM_FILENAME;
        trieFile = basePath + TRIE_FILENAME;

        InputStream is = null;
        OutputStream os = null;

        try{
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
        }catch (FileNotFoundException ex){
            Log.e(TAG, "initDeepspeechModel: Cannot find file", ex);
        }catch (IOException ex){
            Log.e(TAG, "initDeepspeechModel: IOException caught", ex);
        }

        model = new DeepSpeechModel(modelFile, BEAM_WIDTH);
        model.enableDecoderWihLM(lmFile, trieFile, LM_ALPHA, LM_BETA);
    }

    private void initExtractionProvider(){
        entityExtractionProvider = new MlKitEntityExtractionProvider();

        filterEntities = new ArrayList<>();
        filterEntities.addAll(entityExtractionProvider.getHandledEntities());
    }

    private void initTestFiles() throws IOException {
        filesToTest = new ArrayList<>();

        filesToTest.add(new TestRecord(it.cnr.iit.anonymousvoice.test.R.raw.e0001, "call me tomorrow", 1, "e0001.wav"));
        filesToTest.add(new TestRecord(it.cnr.iit.anonymousvoice.test.R.raw.e0002, "the pen is on the table", 0, "e0002.wav"));
        filesToTest.add(new TestRecord(it.cnr.iit.anonymousvoice.test.R.raw.e0003, "it rained yesterday", 1, "e0003.wav"));
        filesToTest.add(new TestRecord(it.cnr.iit.anonymousvoice.test.R.raw.e0004, "i have paid six dollars for a coffee", 1, "e0004.wav"));
        filesToTest.add(new TestRecord(it.cnr.iit.anonymousvoice.test.R.raw.e0005, "i live in italy", 0, "e0005.wav"));
        filesToTest.add(new TestRecord(it.cnr.iit.anonymousvoice.test.R.raw.e0006, "hello mark send me and email at mark@gmail.com", 1, "e0006.wav"));
        filesToTest.add(new TestRecord(it.cnr.iit.anonymousvoice.test.R.raw.e0007, "Hello I’m lost Could you help me please I must reach Main Street new York", 1, "e0007.wav"));
        filesToTest.add(new TestRecord(it.cnr.iit.anonymousvoice.test.R.raw.e0008, "You should go by metro", 0, "e0008.wav"));
        filesToTest.add(new TestRecord(it.cnr.iit.anonymousvoice.test.R.raw.e0009, "The flight CFG2162 is departing from gate 15", 1, "e0009.wav"));
        filesToTest.add(new TestRecord(it.cnr.iit.anonymousvoice.test.R.raw.e0010, "You can contact the test team tomorrow at info@supportteam.com to determine the best timeline", 2, "e0010.wav"));
        filesToTest.add(new TestRecord(it.cnr.iit.anonymousvoice.test.R.raw.e0011, "I met Andrew yesterday near Main Street in Prato", 2, "e0011.wav"));
        filesToTest.add(new TestRecord(it.cnr.iit.anonymousvoice.test.R.raw.e0012, "The total of the order is 100 euro You order will be delivered from tomorrow", 2, "e0012.wav"));
        filesToTest.add(new TestRecord(it.cnr.iit.anonymousvoice.test.R.raw.e0013, "Have you ever met a famous person", 0, "e0013.wav"));
        filesToTest.add(new TestRecord(it.cnr.iit.anonymousvoice.test.R.raw.e0014, "The 1st May will held an important meeting at Main Street 30", 2, "e0014.wav"));
        filesToTest.add(new TestRecord(it.cnr.iit.anonymousvoice.test.R.raw.e0015, "Don’t worry Mary is going to call the restaurant to make a reservation for dinner", 0, "e0015.wav"));
        filesToTest.add(new TestRecord(it.cnr.iit.anonymousvoice.test.R.raw.e0016, "Your order has shipped To follow the progress of your delivery please use this tracking number 12345", 1, "e0016.wav"));
        filesToTest.add(new TestRecord(it.cnr.iit.anonymousvoice.test.R.raw.e0017, "It may costs up to 50 dollars for going to the stadium by taxi from here", 1, "e0017.wav"));
        filesToTest.add(new TestRecord(it.cnr.iit.anonymousvoice.test.R.raw.e0018, "As Hemingway said My only regret in life is that I did not drink more wine", 0, "e0018.wav"));
        filesToTest.add(new TestRecord(it.cnr.iit.anonymousvoice.test.R.raw.e0019, "That mobile phone costs an arm and a leg 1000 pounds are not enough", 1, "e0019.wav"));
        filesToTest.add(new TestRecord(it.cnr.iit.anonymousvoice.test.R.raw.e0020, "Hurry up or we lose the train", 0, "e0020.wav"));


        for(TestRecord tr : filesToTest){
            InputStream is = FileUtil.getResourceStream(InstrumentationRegistry.getInstrumentation(),
                    tr.getResourceId());
            OutputStream os = new FileOutputStream(basePath + tr.getFilename());
            FileUtil.copy(is, os);
            is.close();
            os.close();
        }
    }

    @After
    public void clean(){
        //Remove deepspeech files
        FileUtil.removeFile(modelFile);
        FileUtil.removeFile(lmFile);
        FileUtil.removeFile(trieFile);
        //Remove test files
        filesToTest.forEach(f -> {
            String filePath = basePath + f.getFilename();
            FileUtil.removeFile(filePath);
        });
    }

    /**
     * Executes an integration test between deepspeech and entity extraction.
     * The test works as follow: first, it takes a list of file with an audio and a reference sentence.
     * Each file is passed to deepspeech in order to obtain an hypothesis sentence. Than we calculate
     * wer index.
     * Lastly each hypothesis sentence is given in input to entity extraction and we check how many entities
     * have been recognized
     */
    @Test
    public void integrationTest() throws InterruptedException {
        final int processedRecord[] = {0};
        int testRecordNumber = filesToTest.size();
        int attempts = 0;
        int maxAttempts = 10;

        List<TestResult> results = new ArrayList<>();

        filesToTest.forEach(f ->{
            String reference = f.getReference();
            String audioFile = basePath + f.getFilename();
            String hypothesis = null;
            try {
                hypothesis = DeepspeechUtil.inference(audioFile, model);
            } catch (IOException e) {
                Log.e(TAG, "integrationTest: Error while performing speech to text!", e);
                e.printStackTrace();
            }

            float wer = getWer(reference, hypothesis);

            TestResult res = new TestResult();
            res.setReference(reference);
            res.setHypothesis(hypothesis);
            res.setWordErrorRate(wer);
            results.add(res);

            entityExtractionProvider.extract(hypothesis, this.language, this.filterEntities, new EntityExtractionCallbacks() {
                @Override
                public void onExtractionSuccess(TextExtractionResult result) {
                    results.forEach(testResult -> {
                        if(testResult.getHypothesis().equals(result.getOriginalText())){
                            testResult.setExtractionResult(result);
                        }
                    });
                    processedRecord[0]++;
                }

                @Override
                public void onExtractionError(String errorMessage) {
                    Log.e(TAG, "onExtractionError: Error while extracting entities");
                    throw new RuntimeException(errorMessage);
                }
            });
        });

        //Waiting for all test records to be processed
        while(processedRecord[0] < testRecordNumber && attempts < maxAttempts ){
            Thread.sleep(2000);
            attempts++;
        }

        try{
            printResults(results);
        }catch (IOException ex){
            Log.e(TAG, "integrationTest: Error while writing result file!", ex);
        }
    }

    private float getWer(String reference, String hypothesis) {
        float wer = 0f;

        WordSequenceAligner werEval = new WordSequenceAligner();
        List<WordSequenceAligner.Alignment> alignments = new ArrayList<>();
        alignments.add(werEval.align(reference.split(" "), hypothesis.split(" ")));
        WordSequenceAligner.SummaryStatistics ss = werEval.new SummaryStatistics(alignments);

        wer = ss.getWordErrorRate();

        return wer;
    }

    private void printResults(List<TestResult> results) throws IOException {
        final String fieldSep = ";";
        final String newLine = "\n";

        DecimalFormat df = new DecimalFormat("##0.###");
        df.setRoundingMode(RoundingMode.DOWN);

        FileWriter csvWriter = new FileWriter(basePath + RESULT_FILENAME);

        //File header
        csvWriter.append("Reference")
                .append(fieldSep)
                .append("Hypothesis")
                .append(fieldSep)
                .append("Word Error Rate")
                .append(fieldSep)
                .append("# Recognized entities")
                .append(fieldSep)
                .append("Entities")
                .append(newLine);

        for(TestResult res : results){
            String entities = "";
            int recognized = res.getExtractionResult().getExtractedEntities().size();

            for(EntityExtractionResult entityExtractionResult : res.getExtractionResult().getExtractedEntities()){
                entities += entityExtractionResult.getOriginalEntityText() + ",";
            }

            //Remove last comma
            entities.substring(0, (entities.isEmpty() ? 0 : entities.length() - 1 ));

            csvWriter.append(res.getReference())
                    .append(fieldSep)
                    .append(res.getHypothesis())
                    .append(fieldSep)
                    .append(df.format(res.getWordErrorRate()))
                    .append(fieldSep)
                    .append(String.valueOf(recognized))
                    .append(fieldSep)
                    .append(entities)
                    .append(newLine);
        }

        csvWriter.flush();
        csvWriter.close();
    }
}
