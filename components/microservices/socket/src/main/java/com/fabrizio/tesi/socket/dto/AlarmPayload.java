package com.fabrizio.tesi.socket.dto;

import java.util.Date;

import com.fabrizio.tesi.socket.jsonutil.DateDeserializer;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

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
    @JsonDeserialize(using = DateDeserializer.class)
    Date time;
    @JsonDeserialize(using = DateDeserializer.class)
    Date lastHB;
}
