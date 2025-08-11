package by.cleverdev_task.util;

import lombok.experimental.UtilityClass;

/**
 * Utility class containing error messages.
 */
@UtilityClass
public class ErrorMessages {
    public static final String FETCH_CLIENTS_FAILED = "Failed to fetch clients: status=%s";
    public static final String FETCH_NOTES_FAILED = "Failed to fetch notes: status=%s";
    public static final String EXTERNAL_CALL_FAILED = "Error calling external system %s";
}
