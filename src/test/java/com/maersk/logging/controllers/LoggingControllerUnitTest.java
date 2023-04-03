package com.maersk.logging.controllers;

import com.maersk.logging.models.LoggingMetadata;
import com.maersk.logging.models.LoggingMetadataRequest;
import com.maersk.logging.models.Severity;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class LoggingControllerUnitTest {

    @Test
    void createLogWithOptionalFields() {
        LoggingController loggingController = new LoggingController();
        LoggingMetadata loggingMetadata = new LoggingMetadata(
                UUID.randomUUID(),
                UUID.randomUUID(),
                Severity.DEBUG,
                LocalDateTime.now(ZoneOffset.UTC).toString(),
                "message",
                "component name",
                UUID.randomUUID()
        );
        LoggingMetadataRequest loggingMetadataRequest = new LoggingMetadataRequest(loggingMetadata, false, false, false);
        ResponseEntity<LoggingMetadata> response = loggingController.createLog(loggingMetadataRequest);
        assertEquals(HttpStatusCode.valueOf(201), response.getStatusCode());
        assertEquals(loggingMetadata.toString(), response.getBody().toString());
    }

    @Test
    void createLogWithoutOptionalFields() {
        LoggingController loggingController = new LoggingController();
        LoggingMetadata loggingMetadata = new LoggingMetadata(
                UUID.randomUUID(),
                UUID.randomUUID(),
                Severity.DEBUG,
                LocalDateTime.now(ZoneOffset.UTC).toString(),
                "message"
        );
        LoggingMetadataRequest loggingMetadataRequest = new LoggingMetadataRequest(loggingMetadata, false, false, false);
        ResponseEntity<LoggingMetadata> response = loggingController.createLog(loggingMetadataRequest);
        assertEquals(HttpStatusCode.valueOf(201), response.getStatusCode());
        assertEquals(loggingMetadata.toString(), response.getBody().toString());
    }

    @Test
    void createLogWithoutData() {
        LoggingController loggingController = new LoggingController();
        LoggingMetadataRequest loggingMetadataRequest = new LoggingMetadataRequest(null, false, false, false);
        ResponseEntity<LoggingMetadata> response = loggingController.createLog(loggingMetadataRequest);
        assertEquals(HttpStatusCode.valueOf(204), response.getStatusCode());
    }

    @Test
    void getLogFromCurrentFile() {
        // TODO: Implement me
    }

    @Test
    void getLogFromHistoryFiles() {
        // TODO: Implement me
    }
}