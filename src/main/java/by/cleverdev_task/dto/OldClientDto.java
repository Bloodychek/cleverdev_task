package by.cleverdev_task.dto;

/**
 * DTO representing a client from the old system.
 */
public record OldClientDto(String agency, String guid, String firstName, String lastName,
                           String status, String dob, String createdDateTime) {
}
