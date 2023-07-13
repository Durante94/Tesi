package com.fabrizio.tesi.gateway.dto;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

@FieldDefaults(level = AccessLevel.PRIVATE)
@NoArgsConstructor
@Setter
public class CallbackRequest {
    @Getter
    String code;
    String session_state;
}