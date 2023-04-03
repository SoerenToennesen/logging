package com.maersk.logging.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LoggingMetadata {
    private UUID applicationId;
    private UUID traceId;
    private Severity severity;
    private String timestamp; // Should always be UTC
    private String message;
    private String componentName; // Optional
    private UUID requestId; // Optional

    public LoggingMetadata(UUID applicationId, UUID traceId, Severity severity, String timestamp, String message) {
        this.applicationId = applicationId;
        this.traceId = traceId;
        this.severity = severity;
        this.timestamp = timestamp;
        this.message = message;
    }

    @Override
    public String toString() {
        return "ApplicationID " + applicationId + "," +
                "TraceID " + traceId + "," +
                "Severity " + severity.name() + "(" + severity.getNumVal() + ")" + "," +
                "Timestamp " + timestamp + "," +
                "Message " + message + "," +
                (componentName != null ? ("ComponentName " + componentName + ",") : "") +
                (requestId != null ? ("RequestID " + requestId + ",") : "") +
                "\n";
    }

}
