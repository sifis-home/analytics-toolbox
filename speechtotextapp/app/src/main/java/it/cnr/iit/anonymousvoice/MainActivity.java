package it.cnr.iit.anonymousvoice;

import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.ArrayList;

import it.cnr.iit.anonymousvoice.entityextraction.EntityExtractionProvider;
import it.cnr.iit.anonymousvoice.entityextraction.filter.FilterEntity;
import it.cnr.iit.anonymousvoice.entityextraction.provider.googlemlkit.MlKitEntityExtractionProvider;
import it.cnr.iit.anonymousvoice.speechrecognition.SpeechToTextProvider;
import it.cnr.iit.anonymousvoice.speechrecognition.provider.deepspeech.DeepSpeechProvider;
import it.cnr.iit.anonymousvoice.enums.LanguageEnum;
import it.cnr.iit.anonymousvoice.store.OptionsStore;
import it.cnr.iit.anonymousvoice.texttospeech.TextToSpeechProvider;
import it.cnr.iit.anonymousvoice.texttospeech.provider.googletts.GoogleTTSProvider;

public class MainActivity extends AppCompatActivity {

    /**
     * Only for debug purpose!
     */
    private static boolean STORE_SAMPLE = false;

    private static final String TAG = "MainActivity";

    private static LanguageEnum LANGUAGE = LanguageEnum.US_ENGLISH;

    private BottomNavigationView navigationView;

    private HomeFragment homeFragment;

    private FilterFragment filterFragment;

    private SpeechToTextProvider speechToTextProvider;

    private EntityExtractionProvider nerProvider;

    private TextToSpeechProvider textToSpeechProvider;

    private OptionsStore optionsStore = new OptionsStore();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        this.nerProvider = new MlKitEntityExtractionProvider();
        this.speechToTextProvider = new DeepSpeechProvider(getApplicationContext(), LANGUAGE,
                STORE_SAMPLE, false);
        this.textToSpeechProvider = new GoogleTTSProvider(getApplicationContext());

        this.optionsStore.setSelectedEntity(nerProvider.getHandledEntities());

        this.homeFragment = HomeFragment.newInstance(LANGUAGE, speechToTextProvider, nerProvider,
                textToSpeechProvider, optionsStore.getSelectedEntity());
        this.filterFragment = FilterFragment.newInstance(nerProvider.getHandledEntities(), optionsStore.getSelectedEntity());

        this.navigationView = findViewById(R.id.bottomNavigationView);

        bind();
        loadFragment(homeFragment);
    }

    private void bind() {
        navigationView.setOnItemSelectedListener(item -> {
            switch (item.getItemId()) {
                case R.id.home:
                    loadFragment(homeFragment);
                    return true;
                case R.id.filter:
                    loadFragment(filterFragment);
                    return true;
            }
            return false;
        });
    }

    private void loadFragment(Fragment fragmentToLoad) {
        getSupportFragmentManager().beginTransaction().replace(R.id.flFragment, fragmentToLoad).commit();
    }
}