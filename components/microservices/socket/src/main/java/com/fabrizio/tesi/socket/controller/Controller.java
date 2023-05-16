package com.fabrizio.tesi.socket.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.fabrizio.tesi.socket.dto.AlarmDTO;
import com.fabrizio.tesi.socket.dto.AlarmPayload;
import com.fabrizio.tesi.socket.dto.ConfigRespDTO;
import com.fabrizio.tesi.socket.dto.ConfigRespPayload;
import com.fabrizio.tesi.socket.dto.MessageDTO;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;

@RestController
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Controller {
    @Autowired
    SimpMessagingTemplate template;

    @PostMapping
    public ResponseEntity<Void> sendConfig(@RequestBody ConfigRespPayload message) {
        template.convertAndSend("/topic/configResponse", new ConfigRespDTO(message));
        return ResponseEntity.ok().build();
    }

    @PostMapping("/alarms/{id}")
    public ResponseEntity<Void> sendAlarm(@PathVariable("id") String id, @RequestBody AlarmPayload message) {
        message.setId(id);
        template.convertAndSend("/topic/alarm", new AlarmDTO(message));
        return ResponseEntity.ok().build();
    }

    @MessageMapping("/sendMessage")
    public void receiveMessage(@Payload MessageDTO<String> textMessageDTO) {
        // receive message from client
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
