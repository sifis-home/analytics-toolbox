package it.cnr.iit.anonymousvoice.speechrecognition.provider.deepspeech.task;

/**
 * Result class for preparing model task
 */
public class PrepareModelResult {
    private boolean isFinish = true;

    public boolean isFinish() {
        return isFinish;
    }

    public void setFinish(boolean finish) {
        isFinish = finish;
    }
}
