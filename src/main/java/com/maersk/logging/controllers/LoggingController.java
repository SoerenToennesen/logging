package com.maersk.logging.controllers;

import com.maersk.logging.models.LoggingMetadata;
import com.maersk.logging.models.LoggingMetadataRequest;
import com.maersk.logging.services.LoggingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@CrossOrigin(origins = "*", allowedHeaders = "*")
@RestController
@RequestMapping("/api/logging")
public class LoggingController {

    @Autowired
    LoggingService loggingService;

    @PostMapping("/create")
    public ResponseEntity<LoggingMetadata> createLog(@RequestBody LoggingMetadataRequest loggingMetadataRequest) {
        try {
            LoggingMetadata loggingMetadata = loggingMetadataRequest.getLoggingMetadata();
            if (loggingMetadata == null) return new ResponseEntity<>(HttpStatus.NO_CONTENT);
            if (loggingMetadataRequest.getLogToFlatfile()) loggingService.writeToFlatFile(loggingMetadata);
            if (loggingMetadataRequest.getLogToKafka()) loggingService.sendToKafkaTopic(loggingMetadata);
            if (loggingMetadataRequest.getLogToRabbitMQ()) loggingService.sendToRabbitMQTopic(loggingMetadata);
            return new ResponseEntity<>(loggingMetadata, HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/get/{id}")
    public ResponseEntity<LoggingMetadata> getLogFromCurrentFile(@PathVariable("id") UUID id) {
        try {
            LoggingMetadata loggingMetadata = loggingService.getLogFromCurrentLogFile(id);
            if (loggingMetadata == null) return new ResponseEntity<>(HttpStatus.NO_CONTENT);
            return new ResponseEntity<>(loggingMetadata, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/gethistory/{id}")
    public ResponseEntity<LoggingMetadata> getLogFromHistoryFiles(@PathVariable("id") UUID id) {
        try {
            LoggingMetadata loggingMetadata = loggingService.getLogFromHistoryFiles(id);
            if (loggingMetadata == null) return new ResponseEntity<>(HttpStatus.NO_CONTENT);
            return new ResponseEntity<>(loggingMetadata, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

}
