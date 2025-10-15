package ch.vaudoise.apifactory.common.exception;
/**
 * Application-level exception indicating a client error (HTTP 400).
 * <p>
 * Used for explicit business rule violations not covered by Bean Validation.
 */

public class BadRequestException extends RuntimeException {
    public BadRequestException(String message) {
        super(message);
    }
}
