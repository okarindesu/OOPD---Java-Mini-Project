package exceptions;

public class InvalidRobotStateException extends RuntimeException {
    public InvalidRobotStateException(String message) {
        super(message);
    }
}