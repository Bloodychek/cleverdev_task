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
import by.cleverdev_task.service.OldSystemClient;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ImportServiceImplTest {

    @Mock
    private OldSystemClient oldSystemClient;

    @Mock
    private PatientProfileRepository patientRepo;

    @Mock
    private CompanyUserRepository userRepo;

    @Mock
    private PatientNoteRepository noteRepo;

    @Mock
    private PatientNoteMapper noteMapper;

    @InjectMocks
    private ImportServiceImpl importService;

    @Test
    void importAllNotes() {

        // Arrange
        OldClientDto client = new OldClientDto("agency", "client-guid", "John", "Doe", "active", "1990-01-01", "2020-01-01 12:00:00");
        OldNoteDto noteDto = new OldNoteDto("Test note", "note-guid", "2025-01-01 10:00:00", "client-guid", "2025-01-01 10:00:00", "test_user", "2025-01-01 10:00:00");
        PatientProfile patient = new PatientProfile();
        patient.setId(1L);
        patient.setStatusId((short) 200);

        CompanyUser user = new CompanyUser();
        user.setId(1L);
        user.setLogin("test_user");

        PatientNote newNote = new PatientNote();
        newNote.setGuid("note-guid");

        when(oldSystemClient.getAllClients()).thenReturn(List.of(client));
        when(oldSystemClient.getNotes(any(), any(), any(), any())).thenReturn(List.of(noteDto));
        when(patientRepo.findByOldClientGuid("client-guid")).thenReturn(List.of(patient));
        when(userRepo.findByLogin("test_user")).thenReturn(Optional.of(user));
        when(noteRepo.findByGuid("note-guid")).thenReturn(Optional.empty());
        when(noteMapper.fromDto(noteDto)).thenReturn(newNote);

        // Act
        importService.importAllNotes();

        // Assert
        verify(noteRepo).save(argThat(note ->
                note.getGuid().equals("note-guid")
                        && note.getPatient().equals(patient)
                        && note.getCreatedByUser().equals(user)
                        && note.getLastModifiedByUser().equals(user)
        ));
    }
}
