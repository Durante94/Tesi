package com.fabrizio.tesi.socket;

import com.fabrizio.tesi.socket.controller.Controller;
import com.fabrizio.tesi.socket.dto.*;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.messaging.simp.SimpMessagingTemplate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
public class ControllerTest {
    @MockBean
    SimpMessagingTemplate socketTemplate;

    @MockBean
    KafkaTemplate<String, ConfigReqDTO> kafkaTemplate;

    @Autowired
    Controller controller;

    ObjectMapper objectMapper = new ObjectMapper();

    @Test
    void sendConfigTest() throws JsonProcessingException {
        int amplitude = 51, frequency = 28;
        String function = "tan", headerValue = "test";
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
            assertEquals(headerValue, arg1.getPayload().getAgentId());
            assertEquals(amplitude, arg1.getPayload().getAmplitude());
            assertEquals(frequency, arg1.getPayload().getFrequency());
            assertEquals(function, arg1.getPayload().getFunction());
            return null;
        }).when(socketTemplate).convertAndSend(Mockito.anyString(), Mockito.any(ConfigRespDTO.class));

        controller.sendConfig(objectMapper.readValue(json, ConfigRespPayload.class), headerValue);
    }

    @Test
    void sendAlarmTest() throws JsonProcessingException {
        long time = 1687466895L, lastHB = 1687466895L;
        String type = "testType", headerValue = "test";
        String json = String.format("{\n" +
                "\t\"type\": \"%s\",\n" +
                "\t\"time\": %d,\n" +
                "\t\"lastHB\": %d\n" +
                "}", type, time, lastHB);

        Mockito.doAnswer(invocation -> {
            String arg0 = invocation.getArgument(0, String.class);
            AlarmDTO arg1 = invocation.getArgument(1, AlarmDTO.class);

            assertEquals(arg0, "/topic/alarm");
            assertNotNull(arg1);
            assertNotNull(arg1.getPayload());
            assertEquals(headerValue, arg1.getPayload().getId());
            assertEquals(time, arg1.getPayload().getTime().getTime() / 1000);
            assertEquals(lastHB, arg1.getPayload().getLastHB().getTime() / 1000);
            assertEquals(type, arg1.getPayload().getType());
            return null;
        }).when(socketTemplate).convertAndSend(Mockito.anyString(), Mockito.any(AlarmDTO.class));

        controller.sendAlarm(headerValue, objectMapper.readValue(json, AlarmPayload.class));
    }

    @Test
    void broadcastConfigTest() throws JsonProcessingException {
        int amplitude = 51, frequency = 28;
        String function = "tan";
        String json = String.format("{\n" +
                "\t\"amplitude\": %d,\n" +
                "\t\"frequency\": %d,\n" +
                "\t\"function\": \"%s\"\n" +
                "}", amplitude, frequency, function);

        ConfigRespDTO test = controller.broadcastConfig(new ConfigRespDTO(objectMapper.readValue(json, ConfigRespPayload.class)));

        assertNotNull(test);
        assertEquals("config-resp", test.getType());
        assertNotNull(test.getPayload());
        assertEquals(amplitude, test.getPayload().getAmplitude());
        assertEquals(frequency, test.getPayload().getFrequency());
        assertEquals(function, test.getPayload().getFunction());
    }

    @Test
    void broadcastAlarmTest() throws JsonProcessingException {
        long time = 1687466895L, lastHB = 1687466895L;
        String type = "testType";
        String json = String.format("{\n" +
                "\t\"type\": \"%s\",\n" +
                "\t\"time\": %d,\n" +
                "\t\"lastHB\": %d\n" +
                "}", type, time, lastHB);

        AlarmDTO test = controller.broadcastAlarm(new AlarmDTO(objectMapper.readValue(json, AlarmPayload.class)));

        assertNotNull(test);
        assertEquals("alarm", test.getType());
        assertNotNull(test.getPayload());
        assertEquals(time, test.getPayload().getTime().getTime() / 1000);
        assertEquals(lastHB, test.getPayload().getLastHB().getTime() / 1000);
        assertEquals(type, test.getPayload().getType());
    }

    @Test
    void recieveMessageTest() throws JsonProcessingException {
        String agentId = "test", json = String.format("{\"id\": \"%s\"}", agentId);

        Mockito.doAnswer(invocation -> {
            String arg0 = invocation.getArgument(0, String.class);
            String arg1 = invocation.getArgument(1, String.class);
            ConfigReqDTO arg2 = invocation.getArgument(2, ConfigReqDTO.class);

            assertEquals(arg0, "config-request");
            assertEquals(arg1, "request");
            assertNotNull(arg2);
            assertEquals(agentId, arg2.getId());
            return null;
        }).when(kafkaTemplate).send(Mockito.anyString(), Mockito.anyString(), Mockito.any(ConfigReqDTO.class));

        controller.receiveMessage(objectMapper.readValue(json, ConfigReqDTO.class));
    }
}
