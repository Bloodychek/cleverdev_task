package by.cleverdev_task.service;

import by.cleverdev_task.dto.OldClientDto;
import by.cleverdev_task.dto.OldNoteDto;
import by.cleverdev_task.exception.ExternalSystemException;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static by.cleverdev_task.util.Constants.API_URL;
import static by.cleverdev_task.util.ErrorMessages.EXTERNAL_CALL_FAILED;
import static by.cleverdev_task.util.ErrorMessages.FETCH_CLIENTS_FAILED;
import static by.cleverdev_task.util.ErrorMessages.FETCH_NOTES_FAILED;

/**
 * Client for communicating with the legacy system via HTTP.
 * Used to fetch clients and notes from the old system.
 */
@Service
@AllArgsConstructor
public class OldSystemClient {

    private final RestTemplate restTemplate;

    public List<OldClientDto> getAllClients() {
        try {
            ResponseEntity<OldClientDto[]> response = restTemplate.postForEntity(
                    API_URL + "/clients", null, OldClientDto[].class);

            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                return Arrays.asList(response.getBody());
            } else {
                throw new ExternalSystemException(FETCH_CLIENTS_FAILED + response.getStatusCode());
            }
        } catch (RestClientException e) {
            throw new ExternalSystemException(EXTERNAL_CALL_FAILED, e);
        }
    }

    public List<OldNoteDto> getNotes(String agency, String clientGuid, String dateFrom, String dateTo) {
        Map<String, String> payload = Map.of(
                "agency", agency,
                "clientGuid", clientGuid,
                "dateFrom", dateFrom,
                "dateTo", dateTo
        );

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<Map<String, String>> request = new HttpEntity<>(payload, headers);

        try {
            ResponseEntity<OldNoteDto[]> response = restTemplate.postForEntity(
                    API_URL + "/notes", request, OldNoteDto[].class);

            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                return Arrays.asList(response.getBody());
            } else {
                throw new ExternalSystemException(FETCH_NOTES_FAILED + response.getStatusCode());
            }
        } catch (RestClientException e) {
            throw new ExternalSystemException(EXTERNAL_CALL_FAILED, e);
        }
    }
}