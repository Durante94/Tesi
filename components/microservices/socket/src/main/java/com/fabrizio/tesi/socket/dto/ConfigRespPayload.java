package com.fabrizio.tesi.socket.dto;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.FieldDefaults;

@FieldDefaults(level = AccessLevel.PRIVATE)
@Getter
public class ConfigRespPayload {
    String agentId;
    float amplitude;
    float frequency;
    String function;
}