package it.cnr.iit.anonymousvoice;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import it.cnr.iit.anonymousvoice.entityextraction.EntityExtractionCallbacks;
import it.cnr.iit.anonymousvoice.entityextraction.EntityExtractionProvider;
import it.cnr.iit.anonymousvoice.entityextraction.filter.FilterEntity;
import it.cnr.iit.anonymousvoice.entityextraction.result.TextExtractionResult;
import it.cnr.iit.anonymousvoice.entityextraction.result.entityanonymization.StandardAnonymization;
import it.cnr.iit.anonymousvoice.enums.LanguageEnum;
import it.cnr.iit.anonymousvoice.speechrecognition.SpeechToTextLifecycleCallbacks;
import it.cnr.iit.anonymousvoice.speechrecognition.SpeechToTextProvider;
import it.cnr.iit.anonymousvoice.speechrecognition.SpeechToTextResult;
import it.cnr.iit.anonymousvoice.speechrecognition.provider.deepspeech.task.PrepareModelResult;
import it.cnr.iit.anonymousvoice.speechrecognition.provider.deepspeech.task.PrepareModelTaskRunner;
import it.cnr.iit.anonymousvoice.texttospeech.TextToSpeechError;
import it.cnr.iit.anonymousvoice.texttospeech.TextToSpeechLifecycleCallbacks;
import it.cnr.iit.anonymousvoice.texttospeech.TextToSpeechProvider;

/**
 * Home fragment.
 * Contains business logic to handle voice recording and anonymization
 */
public class HomeFragment extends Fragment {

    private static final String TAG = "HomeFragment";

    public static final String LANGUAGE_PARAM = "language";

    private SpeechToTextProvider speechToTextProvider;

    private EntityExtractionProvider nerProvider;

    private TextToSpeechProvider textToSpeechProvider;

    private List<FilterEntity> filterEntities;

    /**
     * Button to start voice recorder
     */
    private Button voiceRecorderBtn;
    /**
     * Button to start text to speech
     */
    private Button playSpeechBtn;
    /**
     * Button to save anonymize speech
     */
    private Button saveSpeechBtn;
    /**
     * A multitext field that will show the
     * translated speech
     */
    private TextView translatedSpeechTextView;
    /**
     * Used to notify model preparation progress to
     * user
     */
    private ProgressBar progressBar;
    /**
     * A toast to show message
     */
    private Toast toastMsg;

    private LanguageEnum language;

    public static HomeFragment newInstance(LanguageEnum language, SpeechToTextProvider speechToTextProvider,
                                           EntityExtractionProvider nerProvider, TextToSpeechProvider textToSpeechProvider,
                                           List<FilterEntity> filterEntities) {
        HomeFragment fragment = new HomeFragment(speechToTextProvider, nerProvider, textToSpeechProvider, filterEntities);
        Bundle args = new Bundle();
        args.putSerializable(LANGUAGE_PARAM, language);
        fragment.setArguments(args);
        return fragment;
    }

    public HomeFragment() {
        // Required empty public constructor
    }

    public HomeFragment(SpeechToTextProvider speechToTextProvider, EntityExtractionProvider nerProvider,
                        TextToSpeechProvider textToSpeechProvider, List<FilterEntity> filterEntities) {
        this.speechToTextProvider = speechToTextProvider;
        this.nerProvider = nerProvider;
        this.textToSpeechProvider = textToSpeechProvider;
        this.filterEntities = filterEntities;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            this.language = (LanguageEnum) getArguments().getSerializable(LANGUAGE_PARAM);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        toastMsg = new Toast(getActivity().getApplicationContext());

        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        voiceRecorderBtn = getView().findViewById(R.id.voiceRecorderBtn);

        translatedSpeechTextView = getView().findViewById(R.id.plainTextInfo);
        translatedSpeechTextView.setEnabled(false);

        playSpeechBtn = getView().findViewById(R.id.playSpeechBtn);
        playSpeechBtn.setEnabled(false);

        saveSpeechBtn = getView().findViewById(R.id.saveSpeechBtn);
        saveSpeechBtn.setEnabled(false);

        progressBar = getView().findViewById(R.id.progressBar);
        progressBar.setVisibility(ProgressBar.INVISIBLE);

        bind();
    }

    /**
     * Initializes fields events
     */
    private void bind(){
        /**
         * Voice recorder event
         */
        voiceRecorderBtn.setOnTouchListener((view, motionEvent) -> {
            List<String> missingPermissions = checkPermission();
            if(!missingPermissions.isEmpty()){
                askPermissions(missingPermissions);
            }else{
                onPressVoiceRecorderBtn();
            }
            return true;
        });

        /**
         * Listen anonymize speech event
         */
        playSpeechBtn.setOnClickListener(v -> {
            textToSpeechProvider.speak(cleanText(),
                    language,
                    new TextToSpeechLifecycleCallbacks() {
                        @Override
                        public void onSuccess() {
                            //Nothing to do...
                        }

                        @Override
                        public void onError(TextToSpeechError error) {
                            toastMsg.setText(error.getMessage());
                            toastMsg.setDuration(Toast.LENGTH_LONG);
                            toastMsg.show();
                        }
            });
        });

        /**
         * Save anonymize speech event
         */
        saveSpeechBtn.setOnClickListener(v -> {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd_HHmmss");
            String filename = "anonymizeSpeech_" + sdf.format(new Date()) + ".wav";

            File speechFile = new File(getContext().getExternalFilesDir(
                    Environment.DIRECTORY_DOWNLOADS), filename);

            textToSpeechProvider.saveSpeakToFile(cleanText(), language, speechFile,
                    new TextToSpeechLifecycleCallbacks() {
                        @Override
                        public void onSuccess() {
                            toastMsg.setText("Saved file in " + speechFile.getAbsolutePath());
                            toastMsg.setDuration(Toast.LENGTH_LONG);
                            toastMsg.show();
                        }

                        @Override
                        public void onError(TextToSpeechError error) {
                            toastMsg.setText(error.getMessage());
                            toastMsg.setDuration(Toast.LENGTH_LONG);
                            toastMsg.show();
                        }
            });
        });
    }

    /**
     * Check for user permissions
     * @return a list of string with missing permissions.
     */
    private List<String> checkPermission() {
        List<String> missingPermissions = new ArrayList<>();
        if (ActivityCompat.checkSelfPermission(getActivity().getApplicationContext(), Manifest.permission.RECORD_AUDIO)
                != PackageManager.PERMISSION_GRANTED) {
            Log.d(TAG, "checkPermission: Missing RECORD AUDIO permission");
            missingPermissions.add(Manifest.permission.RECORD_AUDIO);
        }
        if (ActivityCompat.checkSelfPermission(getActivity().getApplicationContext(), Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            Log.d(TAG, "checkPermission: Missing READ_EXTERNAL_STORAGE permission");
            missingPermissions.add(Manifest.permission.READ_EXTERNAL_STORAGE);
        }
        if (ActivityCompat.checkSelfPermission(getActivity().getApplicationContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            Log.d(TAG, "checkPermission: Missing WRITE_EXTERNAL_STORAGE permission");
            missingPermissions.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
        }

        return missingPermissions;
    }

    private void askPermissions(List<String> permissionsToAsk) {
        if (permissionsToAsk.size() > 0) {
            Log.d(TAG, "checkPermission: Missing " + permissionsToAsk.size() + " permissions");
            ActivityCompat.requestPermissions(getActivity(), permissionsToAsk.toArray(new String[0]), 123);
        }
    }

    private void onPressVoiceRecorderBtn(){
        Log.d(TAG, "onPressVoiceRecorderBtn: Start of recording voice...");
        setButtonsEnabled(false, false, false);

        translatedSpeechTextView.setText("");

        final SpeechToTextLifecycleCallbacks callbacks = new SpeechToTextLifecycleCallbacks() {
            @Override
            public void onModelMissing() {
                translatedSpeechTextView.setText("Missing model for speech recognition. \n Copy is going to start soon");
                progressBar.setVisibility(ProgressBar.VISIBLE);

                PrepareModelTaskRunner runner = new PrepareModelTaskRunner();
                runner.executePrepareModelTask(getActivity().getApplicationContext(), language, new PrepareModelTaskRunner.PrepareModelTaskRunnerCallbacks() {
                    @Override
                    public void onUpdate(String updateMessage, int progress) {
                        if(!updateMessage.isEmpty()){
                            translatedSpeechTextView.setText(updateMessage);
                        }
                        progressBar.setProgress(progress);
                    }

                    @Override
                    public void onComplete(PrepareModelResult result) {
                        translatedSpeechTextView.setText("Model is ready!\n Press the button to begin to record");
                        progressBar.setVisibility(ProgressBar.INVISIBLE);
                        setButtonsEnabled(true, false, false);
                    }
                });
            }

            @Override
            public void onError(String errorMessage) {
                toastMsg.setText(errorMessage);
                toastMsg.setDuration(Toast.LENGTH_LONG);
                toastMsg.show();
                setButtonsEnabled(true, false, false);
            }

            @Override
            public void onSuccess(SpeechToTextResult speechToTextResult) {
                translatedSpeechTextView.setText(speechToTextResult.getTranscription());
                startNamedEntityRecognition(speechToTextResult.getTranscription());
            }
        };

        speechToTextProvider.start(callbacks);
    }

    private void startNamedEntityRecognition(String transcription) {
        EntityExtractionCallbacks callbacks = new EntityExtractionCallbacks() {
            @Override
            public void onExtractionSuccess(TextExtractionResult result) {
                Log.d(TAG, "onExtractionSuccess: Entities extraction has found " +
                        result.getExtractedEntities().size() + " entities");

                String anonymizedSpeechText = result.getAnonymizedText(new StandardAnonymization());
                translatedSpeechTextView.setText(anonymizedSpeechText);

                setButtonsEnabled(true, true, true);
            }

            @Override
            public void onExtractionError(String errorMessage) {
                translatedSpeechTextView.setText("Entities extraction error: " + errorMessage);
                setButtonsEnabled(true, false, false);
            }
        };

        nerProvider.extract(transcription, language, filterEntities, callbacks);
    }

    private void setButtonsEnabled(boolean isVoiceRecordEnabled,
                                   boolean isPlaySpeechEnabled,
                                   boolean isSaveSpeechEnabled){
        voiceRecorderBtn.setEnabled(isVoiceRecordEnabled);
        playSpeechBtn.setEnabled(isPlaySpeechEnabled);
        saveSpeechBtn.setEnabled(isSaveSpeechEnabled);
    }

    private String cleanText() {
        String speech = translatedSpeechTextView.getText().toString();
        return speech.replaceAll("\\*", "");
    }
}