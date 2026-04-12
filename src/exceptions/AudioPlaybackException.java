package exceptions;

public class AudioPlaybackException extends RuntimeException {
    public AudioPlaybackException(String message) {
        super(message);
    }
}