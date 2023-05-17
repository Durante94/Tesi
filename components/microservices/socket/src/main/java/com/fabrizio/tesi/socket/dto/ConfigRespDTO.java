package com.fabrizio.tesi.socket.dto;

public class ConfigRespDTO extends MessageDTO<ConfigRespPayload> {
    public ConfigRespDTO(ConfigRespPayload payload) {
        super("config-resp", payload);
    }
}