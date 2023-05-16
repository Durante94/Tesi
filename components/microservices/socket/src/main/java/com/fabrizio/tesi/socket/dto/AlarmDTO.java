package com.fabrizio.tesi.socket.dto;

public class AlarmDTO extends MessageDTO<AlarmPayload> {

    public AlarmDTO(AlarmPayload payload) {
        super("alarm", payload);
    }
}
