package com.maersk.logging.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.maersk.logging.models.LoggingMetadata;
import com.maersk.logging.models.LoggingMetadataRequest;
import com.maersk.logging.models.Severity;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.nio.charset.Charset;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

@ExtendWith(SpringExtension.class)
@WebMvcTest(LoggingController.class)
class LoggingControllerIntegrationTest {

    @SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private LoggingController loggingController;

    private static final String BASE_URL = "/api/logging/";
    public static final MediaType APPLICATION_JSON_UTF8 = new MediaType(MediaType.APPLICATION_JSON.getType(), MediaType.APPLICATION_JSON.getSubtype(), Charset.forName("utf8"));

    @Test
    public void createLogWithData() throws Exception {
        String url = BASE_URL + "/create";
        LoggingMetadata loggingMetadata = new LoggingMetadata(
                UUID.randomUUID(),
                UUID.randomUUID(),
                Severity.DEBUG,
                "2015-07-14T11:42:12.000",
                "message",
                "component name",
                UUID.randomUUID()
        );
        LoggingMetadataRequest loggingMetadataRequest = new LoggingMetadataRequest(loggingMetadata, false, false, false);
        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(SerializationFeature.WRAP_ROOT_VALUE, false);
        ObjectWriter ow = mapper.writer().withDefaultPrettyPrinter();
        String requestJson = ow.writeValueAsString(loggingMetadataRequest);
        MvcResult mvcResult = mockMvc.perform(post(url).contentType(APPLICATION_JSON_UTF8).content(requestJson)).andReturn();
        assertEquals(HttpStatusCode.valueOf(200), mvcResult.getResponse().getStatus());
    }

    @Test
    void createLogWithoutData() throws Exception {
        String url = BASE_URL + "/create";
        MvcResult mvcResult = mockMvc.perform(post(url).contentType(APPLICATION_JSON_UTF8)).andReturn();
        assertEquals(HttpStatusCode.valueOf(400), mvcResult.getResponse().getStatus());
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