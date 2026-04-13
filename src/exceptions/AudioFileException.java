package exceptions;

public class AudioFileException extends RuntimeException {
    public AudioFileException(String message) {
        super(message);
    }
}