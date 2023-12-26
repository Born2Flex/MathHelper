package org.task.exceptions;

public class InvalidPropertiesException extends RuntimeException {
    public InvalidPropertiesException(String message) {
        super(message);
    }

    public InvalidPropertiesException(String message, Throwable cause) {
        super(message, cause);
    }
}
