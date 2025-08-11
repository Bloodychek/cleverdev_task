package by.cleverdev_task.mapper;

import by.cleverdev_task.dto.OldNoteDto;
import by.cleverdev_task.entity.PatientNote;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.Named;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

import static by.cleverdev_task.util.Constants.DATE_FORMAT_OLD_SYSTEM;

/**
 * Mapper interface for converting OldNoteDto to PatientNote.
 */
@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface PatientNoteMapper {

    @Mapping(source = "comments", target = "note")
    @Mapping(source = "createdDateTime", target = "createdDateTime", qualifiedByName = "parseInstant")
    @Mapping(source = "modifiedDateTime", target = "lastModifiedDateTime", qualifiedByName = "parseInstant")
    @Mapping(source = "guid", target = "guid")
    PatientNote fromDto(OldNoteDto dto);

    @Named("parseInstant")
    static Instant parseInstant(String dateTimeStr) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(DATE_FORMAT_OLD_SYSTEM)
                .withZone(ZoneId.of("UTC"));

        return LocalDateTime.parse(dateTimeStr, formatter).atZone(ZoneId.of("UTC")).toInstant();
    }
}