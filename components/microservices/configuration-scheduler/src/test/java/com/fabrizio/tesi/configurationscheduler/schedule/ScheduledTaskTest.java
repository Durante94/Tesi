package com.fabrizio.tesi.configurationscheduler.schedule;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.test.context.TestPropertySource;

import com.fabrizio.tesi.configurationscheduler.dto.ConfigRequest;
import com.fabrizio.tesi.configurationscheduler.repository.CRUDRepository;

@SpringBootTest
@AutoConfigureTestDatabase
@TestPropertySource(properties = { "spring.profiles.active=dev" })
public class ScheduledTaskTest {
    @Value("${kafka.consumer-topic}")
    String confRequestTopic;

    @MockBean
    KafkaTemplate<String, ConfigRequest> templateMsg;

    @Autowired
    ScheduledConfigTask task;

    @Autowired
    CRUDRepository crudRepository;

    @Test
    void entitiesCheckTest() {
        Mockito.doAnswer(invocation -> {
            String topic = invocation.getArgument(0);
            String key = invocation.getArgument(1);
            ConfigRequest request = invocation.getArgument(2);

            assertEquals(topic, confRequestTopic);
            assertEquals("toggle", key);
            assertNotNull(request);
            assertFalse(request.isPayload());
            assertNotNull(request.getId());

            return null;
        }).when(templateMsg).send(Mockito.anyString(), Mockito.anyString(), Mockito.any(ConfigRequest.class));

        task.entitiesCheck();

        // CHECK NO SEND AFTER NO UPDATE
        // task.entitiesCheck();

        // Mockito.verify(templateMsg, Mockito.times(6)).send(Mockito.anyString(), Mockito.anyString(),
        //         Mockito.any(ConfigRequest.class));

        // CRUDEntity updated = crudRepository.getById(1L);

        // Mockito.doAnswer(invocation -> {
        //     String topic = invocation.getArgument(0);
        //     String key = invocation.getArgument(1);
        //     ConfigRequest request = invocation.getArgument(2);

        //     assertEquals(topic, confRequestTopic);
        //     assertEquals("toggle", key);
        //     assertNotNull(request);
        //     assertTrue(request.isPayload());
        //     assertNotNull(request.getId());

        //     assertEquals(request.getId(), updated.getAgentId());

        //     return null;
        // }).when(templateMsg).send(Mockito.anyString(), Mockito.anyString(), Mockito.any(ConfigRequest.class));

        // updated.setEnable(true);
        // crudRepository.save(updated);

        // task.entitiesCheck();

        // Mockito.verify(templateMsg, Mockito.times(7)).send(Mockito.anyString(), Mockito.anyString(),
        //         Mockito.any(ConfigRequest.class));
    }
}
