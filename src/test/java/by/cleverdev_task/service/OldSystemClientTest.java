package by.cleverdev_task.service;

import by.cleverdev_task.dto.OldClientDto;
import by.cleverdev_task.dto.OldNoteDto;
import by.cleverdev_task.exception.ExternalSystemException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.contains;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.isNull;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class OldSystemClientTest {

    @Mock
    private RestTemplate restTemplate;

    @InjectMocks
    private OldSystemClient client;

    private OldClientDto[] clientDtoArray;
    private OldNoteDto[] noteDtoArray;

    @BeforeEach
    void setUp() {
        clientDtoArray = new OldClientDto[]{
                new OldClientDto("agency", "guid-123", "John", "Doe", "active", "1990-01-01", "2020-01-01 12:00:00")
        };

        noteDtoArray = new OldNoteDto[]{
                new OldNoteDto("Note text", "note-guid", "2025-01-01 10:00:00", "guid-123", "2025-01-01 10:00:00", "user1", "2025-01-01 10:00:00")
        };
    }

    @Test
    void getAllClients() {
        // Arrange
        ResponseEntity<OldClientDto[]> response = new ResponseEntity<>(clientDtoArray, HttpStatus.OK);
        when(restTemplate.postForEntity(contains("/clients"), isNull(), eq(OldClientDto[].class)))
                .thenReturn(response);

        // Act
        List<OldClientDto> result = client.getAllClients();

        // Assert
        assertEquals(1, result.size());
        assertEquals("guid-123", result.get(0).guid());
    }

    @Test
    void getAllClientsThrowException() {
        // Arrange
        ResponseEntity<OldClientDto[]> response = new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        when(restTemplate.postForEntity(contains("/clients"), isNull(), eq(OldClientDto[].class)))
                .thenReturn(response);

        // Act & Assert
        assertThrows(ExternalSystemException.class, () -> client.getAllClients());
    }

    @Test
    void getAllClientsException_whenRestClientFails() {
        // Arrange
        when(restTemplate.postForEntity(contains("/clients"), isNull(), eq(OldClientDto[].class)))
                .thenThrow(new RestClientException("Connection error"));

        // Act & Assert
        assertThrows(ExternalSystemException.class, () -> client.getAllClients());
    }

    @Test
    void getNotes() {
        // Arrange
        Map<String, String> payload = Map.of(
                "agency", "agency",
                "clientGuid", "guid-123",
                "dateFrom", "2010-01-01",
                "dateTo", "2030-01-01"
        );

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<Map<String, String>> request = new HttpEntity<>(payload, headers);

        ResponseEntity<OldNoteDto[]> response = new ResponseEntity<>(noteDtoArray, HttpStatus.OK);
        when(restTemplate.postForEntity(contains("/notes"), eq(request), eq(OldNoteDto[].class)))
                .thenReturn(response);

        // Act
        List<OldNoteDto> result = client.getNotes("agency", "guid-123", "2010-01-01", "2030-01-01");

        // Assert
        assertEquals(1, result.size());
        assertEquals("note-guid", result.get(0).guid());
    }

    @Test
    void getNotesException() {
        // Arrange
        ResponseEntity<OldNoteDto[]> response = new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        when(restTemplate.postForEntity(contains("/notes"), any(HttpEntity.class), eq(OldNoteDto[].class)))
                .thenReturn(response);

        // Act & Assert
        assertThrows(ExternalSystemException.class, () ->
                client.getNotes("agency", "guid-123", "2010-01-01", "2030-01-01"));
    }

    @Test
    void getNotes_shouldThrowException_whenRestClientFails() {
        // Arrange
        when(restTemplate.postForEntity(contains("/notes"), any(HttpEntity.class), eq(OldNoteDto[].class)))
                .thenThrow(new RestClientException("Timeout"));

        // Act & Assert
        assertThrows(ExternalSystemException.class, () ->
                client.getNotes("agency", "guid-123", "2010-01-01", "2030-01-01"));
    }
}