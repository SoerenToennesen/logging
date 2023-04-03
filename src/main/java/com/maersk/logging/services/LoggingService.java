package com.maersk.logging.services;

import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.maersk.logging.configs.RabbitMQConfiguration;
import com.maersk.logging.models.LoggingMetadata;
import com.maersk.logging.models.Severity;
import org.apache.tomcat.util.http.fileupload.FileUtils;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.UUID;

import static java.nio.file.StandardOpenOption.APPEND;
import static java.nio.file.StandardOpenOption.CREATE;

@Service
public class LoggingService {

    @Autowired
    private RabbitTemplate rabbitTemplate;
    @Autowired
    private KafkaTemplate<String, LoggingMetadata> kafkaTemplate;

    private static String flatFileLocation;
    private static final String KAFKA_TOPIC = "new_topic";

    public LoggingService() {
        flatFileLocation = "./logs/logfile_" +
                LocalDateTime.now(ZoneOffset.UTC).toString().replace(":", "").replace(" ", "") +
                ".log";
    }

    public void writeToFlatFile(LoggingMetadata loggingMetadata) throws IOException {
        Files.writeString(
                Paths.get(flatFileLocation),
                loggingMetadata.toString(),
                CREATE, APPEND
        );
    }

    public void sendToKafkaTopic(LoggingMetadata loggingMetadata) {
        kafkaTemplate.send(KAFKA_TOPIC, loggingMetadata);
    }

    public void sendToRabbitMQTopic(LoggingMetadata loggingMetadata) {
        rabbitTemplate.convertAndSend(RabbitMQConfiguration.EXCHANGE, RabbitMQConfiguration.ROUTING_KEY, loggingMetadata);
    }

    private List<String> getFileNames(List<String> fileNames, Path dir) {
        try(DirectoryStream<Path> stream = Files.newDirectoryStream(dir)) {
            for (Path path : stream) {
                if (path.toFile().isDirectory()) {
                    getFileNames(fileNames, path);
                } else {
                    fileNames.add(path.toAbsolutePath().toString());
                    System.out.println(path.getFileName());
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return fileNames;
    }

    private LoggingMetadata findLogsFromFiles(String file, UUID id) throws IOException {
        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = br.readLine()) != null) {
                if (line.contains(id.toString())) {
                    List<String> attributes = List.of(line.split(","));
                    LoggingMetadata loggingMetadata = new LoggingMetadata();
                    loggingMetadata.setApplicationId(
                            UUID.fromString(List.of(attributes.get(0).split(" ")).get(1))
                    );
                    loggingMetadata.setTraceId(
                            UUID.fromString(List.of(attributes.get(1).split(" ")).get(1))
                    );
                    String severityString = List.of(attributes.get(2).split(" ")).get(1);
                    loggingMetadata.setSeverity(
                            Severity.valueOf(
                                Integer.parseInt(severityString.substring(severityString.indexOf("(")+1, severityString.indexOf(")")))
                            )
                    );
                    loggingMetadata.setTimestamp(
                        List.of(attributes.get(3).split(" ")).get(1)
                    );
                    loggingMetadata.setMessage(
                        List.of(attributes.get(4).split(" ")).get(1)
                    );
                    loggingMetadata.setComponentName(
                        List.of(attributes.get(5).split(" ")).get(1)
                    );
                    loggingMetadata.setRequestId(
                            UUID.fromString(List.of(attributes.get(6).split(" ")).get(1))
                    );
                    return loggingMetadata;
                }
            }
        }
        return null;
    }

    public LoggingMetadata getLogFromHistoryFiles(UUID id) throws IOException {
        List<String> historyLogfiles = getFileNames(new ArrayList<>(), Path.of("./logs/"));
        for (String historyLogfile : historyLogfiles) {
            LoggingMetadata loggingMetadata = findLogsFromFiles("./logs/" + historyLogfile, id);
            if (loggingMetadata != null) return loggingMetadata;
        }
        return null;
    }

    public LoggingMetadata getLogFromCurrentLogFile(UUID id) throws IOException {
        return findLogsFromFiles(flatFileLocation, id);
    }

}
