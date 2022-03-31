package it.cnr.iit.anonymousvoice.speechrecognition.provider.deepspeech.task;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import it.cnr.iit.anonymousvoice.enums.LanguageEnum;

/**
 * Task which performs the copy of model files.
 */
public class PrepareModelTaskRunner {

    private final ExecutorService executor = Executors.newSingleThreadExecutor();
    private final Handler handler = new Handler(Looper.getMainLooper());

    /**
     * Callbacks interface.
     */
    public interface PrepareModelTaskRunnerCallbacks {
        /**
         * Prints an update message
         * @param updateMessage
         * @param progress
         */
        void onUpdate(String updateMessage, int progress);

        /**
         *
         * @param result
         */
        void onComplete(PrepareModelResult result);
    }

    public void executePrepareModelTask(Context applicationContext, LanguageEnum language, PrepareModelTaskRunnerCallbacks callbacks) {
        PrepareModelTask task = new PrepareModelTask(language, applicationContext.getFilesDir(),
                applicationContext.getAssets(), callbacks);

        executor.execute(() -> {
            try {
                final PrepareModelResult result = task.call();
                handler.post(() -> {
                   callbacks.onComplete(result);
                });
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

}
