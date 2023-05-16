package com.fabrizio.tesi.socket.dto;

import java.util.Date;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

@FieldDefaults(level = AccessLevel.PRIVATE)
@Getter
public class AlarmPayload {
    @Setter
    String id;
    String type;
    Date time;
    Date lastHB;
}
