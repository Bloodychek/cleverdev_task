package by.cleverdev_task.dto;

/**
 * DTO representing a note from the old system.
 */
public record OldNoteDto(String comments, String guid, String modifiedDateTime, String clientGuid,
                         String dateTime, String loggedUser, String createdDateTime) {
}
