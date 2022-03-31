package it.cnr.iit.anonymousvoice.texttospeech;

/**
 * Class container for error
 */
public class TextToSpeechError {

    /**
     * A message error
     */
    private String message;

    /**
     * An error code related to message
     */
    private Integer errorCode;

    public TextToSpeechError(String message, Integer errorCode) {
        this.message = message;
        this.errorCode = errorCode;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Integer getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(Integer errorCode) {
        this.errorCode = errorCode;
    }
}
