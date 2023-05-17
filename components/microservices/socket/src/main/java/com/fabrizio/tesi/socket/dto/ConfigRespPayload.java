package com.fabrizio.tesi.socket.dto;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

@FieldDefaults(level = AccessLevel.PRIVATE)
@Getter
public class ConfigRespPayload {
    @Setter
    String agentId;
    float amplitude;
    float frequency;
    String function;
}