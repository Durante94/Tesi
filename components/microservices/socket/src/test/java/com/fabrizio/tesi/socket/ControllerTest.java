package com.fabrizio.tesi.socket;

import com.fabrizio.tesi.socket.controller.Controller;
import com.fabrizio.tesi.socket.dto.ConfigRespDTO;
import com.fabrizio.tesi.socket.dto.ConfigRespPayload;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.messaging.simp.SimpMessagingTemplate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
public class ControllerTest {
    @MockBean
    SimpMessagingTemplate socketTemplate;

    @Autowired
    Controller controller;

    ObjectMapper objectMapper = new ObjectMapper();

    @Test
    void sendConfigTest() throws JsonProcessingException {
        int amplitude = 51, frequency = 28;
        String function = "tan", headervalue = "test";
        String json = String.format("{\n" +
                "\t\"amplitude\": %d,\n" +
                "\t\"frequency\": %d,\n" +
                "\t\"function\": \"%s\"\n" +
                "}", amplitude, frequency, function);

        Mockito.doAnswer(invocation -> {
            String arg0 = invocation.getArgument(0, String.class);
            ConfigRespDTO arg1 = invocation.getArgument(1, ConfigRespDTO.class);

            assertEquals(arg0, "/topic/configResponse");
            assertNotNull(arg1);
            assertNotNull(arg1.getPayload());
            assertEquals(amplitude, arg1.getPayload().getAmplitude());
            assertEquals(frequency, arg1.getPayload().getFrequency());
            assertEquals(function, arg1.getPayload().getFunction());
            return null;
        }).when(socketTemplate).convertAndSend(Mockito.anyString(), Mockito.any(ConfigRespDTO.class));

        controller.sendConfig(objectMapper.readValue(json, ConfigRespPayload.class), headervalue);
    }
}
