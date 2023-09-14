package com.fabrizio.tesi.socket.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestBody;

import com.fabrizio.tesi.socket.dto.AlarmDTO;
import com.fabrizio.tesi.socket.dto.AlarmPayload;
import com.fabrizio.tesi.socket.dto.ConfigReqDTO;
import com.fabrizio.tesi.socket.dto.ConfigRespDTO;
import com.fabrizio.tesi.socket.dto.ConfigRespPayload;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;

@Component
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Controller {
    @Autowired
    SimpMessagingTemplate socketTemplate;

    @Autowired
    KafkaTemplate<String, ConfigReqDTO> kafkaTemplate;

    @KafkaListener(topics = "config-response", containerFactory = "kafkaListenerContainerConfigFactory")
    public ResponseEntity<Void> sendConfig(@RequestBody ConfigRespPayload message,
            @Header(KafkaHeaders.RECEIVED_MESSAGE_KEY) String id) {
        message.setAgentId(id);
        socketTemplate.convertAndSend("/topic/configResponse", new ConfigRespDTO(message));
        return ResponseEntity.ok().build();
    }

    @KafkaListener(topics = "alarm", containerFactory = "kafkaListenerContainerAlarmFactory")
    public ResponseEntity<Void> sendAlarm(@Header(KafkaHeaders.RECEIVED_MESSAGE_KEY) String id,
            @RequestBody AlarmPayload message) {
        message.setId(id);
        socketTemplate.convertAndSend("/topic/alarm", new AlarmDTO(message));
        return ResponseEntity.ok().build();
    }

    @MessageMapping("/configRequest")
    public void receiveMessage(@Payload ConfigReqDTO configRequest) {
        kafkaTemplate.send("config-request", "request", configRequest);
    }

    @SendTo("/topic/configResponse")
    public ConfigRespDTO broadcastConfig(@Payload ConfigRespDTO textMessageDTO) {
        return textMessageDTO;
    }

    @SendTo("/topic/alarm")
    public AlarmDTO broadcastAlarm(@Payload AlarmDTO textMessageDTO) {
        return textMessageDTO;
    }
}
