package exceptions;

public class WeaponNotEquippedException extends RuntimeException {
    public WeaponNotEquippedException(String message) {
        super(message);
    }
}