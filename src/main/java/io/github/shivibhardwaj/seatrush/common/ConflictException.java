package io.github.shivibhardwaj.seatrush.common;

public class ConflictException extends RuntimeException {
    public ConflictException(String message, Throwable cause) {
        super(message, cause);
    }
}
