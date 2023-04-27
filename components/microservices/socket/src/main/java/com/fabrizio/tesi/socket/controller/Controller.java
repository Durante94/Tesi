package com.fabrizio.tesi.socket.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.fabrizio.tesi.socket.dto.MessageDTO;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;

@RestController
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Controller {
    @Autowired
    SimpMessagingTemplate template;

    @PostMapping
    public ResponseEntity<Void> sendMessage(@RequestBody MessageDTO message) {
        template.convertAndSend("/topic/message", message);
        return ResponseEntity.ok().build();
    }

    @MessageMapping("/sendMessage")
    public void receiveMessage(@Payload MessageDTO textMessageDTO) {
        // receive message from client
    }

    @SendTo("/topic/message")
    public MessageDTO broadcastMessage(@Payload MessageDTO textMessageDTO) {
        return textMessageDTO;
    }
}