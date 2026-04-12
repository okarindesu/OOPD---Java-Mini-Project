package exceptions;

public class InvalidVolumeException extends RuntimeException {
    public InvalidVolumeException(String message) {
        super(message);
    }
}