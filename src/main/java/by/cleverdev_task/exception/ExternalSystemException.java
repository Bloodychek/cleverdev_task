package by.cleverdev_task.exception;

/**
 * Exception thrown when there is a problem with the external system.
 */
public class ExternalSystemException extends RuntimeException {
    public ExternalSystemException(String message) {
        super(message);
    }

    public ExternalSystemException(String message, Throwable cause) {
        super(message, cause);
    }
}

