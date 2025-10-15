package ch.vaudoise.apifactory.common.exception;

import org.springframework.http.HttpStatus;
/**
 * Application-level exception indicating a missing resource (HTTP 404).
 */

public class NotFoundException extends RuntimeException {
    public final HttpStatus status = HttpStatus.NOT_FOUND;
    public NotFoundException(String msg){ super(msg); }
}
