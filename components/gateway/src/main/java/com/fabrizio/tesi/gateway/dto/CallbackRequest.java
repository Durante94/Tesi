package com.fabrizio.tesi.gateway.dto;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.FieldDefaults;

@FieldDefaults(level = AccessLevel.PRIVATE)
public class CallbackRequest {
    @Getter
    String code;
    String state;
}