package by.cleverdev_task.service.impl;

import by.cleverdev_task.dto.OldClientDto;
import by.cleverdev_task.dto.OldNoteDto;
import by.cleverdev_task.entity.CompanyUser;
import by.cleverdev_task.entity.PatientNote;
import by.cleverdev_task.entity.PatientProfile;
import by.cleverdev_task.mapper.PatientNoteMapper;
import by.cleverdev_task.repository.CompanyUserRepository;
import by.cleverdev_task.repository.PatientNoteRepository;
import by.cleverdev_task.repository.PatientProfileRepository;
import by.cleverdev_task.service.ImportService;
import by.cleverdev_task.service.OldSystemClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

import static by.cleverdev_task.util.Constants.IMPORT_DATE_FROM;
import static by.cleverdev_task.util.Constants.IMPORT_DATE_TO;
import static by.cleverdev_task.util.Constants.STATUS_ACTIVE;
import static by.cleverdev_task.util.Constants.STATUS_PARTIALLY_ACTIVE;
import static by.cleverdev_task.util.Constants.STATUS_UNDER_OBSERVATION;

/**
 * Service responsible for importing client notes from the old system.
 * Handles mapping, user lookup/creation, and note saving or updating logic.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class ImportServiceImpl implements ImportService {

    private final OldSystemClient oldSystemClient;
    private final PatientProfileRepository patientRepo;
    private final CompanyUserRepository userRepo;
    private final PatientNoteRepository noteRepo;
    private final PatientNoteMapper noteMapper;

    public void importAllNotes() {
        try {
            List<OldClientDto> clients = oldSystemClient.getAllClients();
            log.info("Import: {} clients received", clients.size());

            for (OldClientDto client : clients) {
                processClientNotes(client);
            }
        } catch (Exception e) {
            log.error("Failed to complete import process: {}", e.getMessage(), e);
        }

        log.info("Import completed");
    }

    private void processClientNotes(OldClientDto client) {
        List<OldNoteDto> notes;
        try {
            notes = oldSystemClient.getNotes(
                    client.agency(), client.guid(),
                    IMPORT_DATE_FROM, IMPORT_DATE_TO
            );
        } catch (Exception e) {
            log.error("Failed to get notes for client {}: {}", client.guid(), e.getMessage(), e);
            return;
        }

        for (OldNoteDto dto : notes) {
            try {
                importSingleNote(dto);
            } catch (Exception e) {
                log.error("Error processing note {}: {}", dto.guid(), e.getMessage(), e);
            }
        }
    }

    private void importSingleNote(OldNoteDto dto) {
        List<PatientProfile> patients = patientRepo.findByOldClientGuid(dto.clientGuid());

        if (patients.isEmpty()) {
            return;
        }

        CompanyUser user = userRepo.findByLogin(dto.loggedUser())
                .orElseGet(() -> {
                    CompanyUser newUser = new CompanyUser();
                    newUser.setLogin(dto.loggedUser());
                    return userRepo.save(newUser);
                });

        Instant newModified;
        try {
            newModified = PatientNoteMapper.parseInstant(dto.modifiedDateTime());
        } catch (Exception e) {
            log.warn("Invalid date in note {}", dto.guid());
            return;
        }

        for (PatientProfile patient : patients) {
            if (!isActive(patient.getStatusId())) {
                continue;
            }

            Optional<PatientNote> existingNote = noteRepo.findByGuid(dto.guid());

            if (existingNote.isPresent()) {
                PatientNote note = existingNote.get();
                if (newModified.isAfter(note.getLastModifiedDateTime())) {
                    note.setNote(dto.comments());
                    note.setLastModifiedDateTime(newModified);
                    note.setLastModifiedByUser(user);
                    noteRepo.save(note);
                    log.info("Updated note {}", dto.guid());
                } else {
                    log.info("The note is already relevant: guid={}", dto.guid());
                }
            } else {
                PatientNote note = noteMapper.fromDto(dto);
                note.setPatient(patient);
                note.setCreatedByUser(user);
                note.setLastModifiedByUser(user);
                noteRepo.save(note);
                log.info("New note {}", dto.guid());
            }
        }
    }

    private boolean isActive(Short statusId) {
        return statusId == STATUS_ACTIVE
                || statusId == STATUS_PARTIALLY_ACTIVE
                || statusId == STATUS_UNDER_OBSERVATION;
    }
}