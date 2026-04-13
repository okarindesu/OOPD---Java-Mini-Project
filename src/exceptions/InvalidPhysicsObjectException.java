package exceptions;

public class InvalidPhysicsObjectException extends RuntimeException {
    public InvalidPhysicsObjectException(String message) {
        super(message);
    }
}