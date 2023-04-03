package com.maersk.logging.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LoggingMetadataRequest {

    private LoggingMetadata loggingMetadata;
    private Boolean logToFlatfile;
    private Boolean logToKafka;
    private Boolean logToRabbitMQ;

    @Override
    public String toString() {
        return loggingMetadata.toString();
    }

}
