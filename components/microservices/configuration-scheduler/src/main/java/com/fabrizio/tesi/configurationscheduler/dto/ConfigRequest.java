package com.fabrizio.tesi.configurationscheduler.dto;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

@FieldDefaults(level = AccessLevel.PRIVATE)
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ConfigRequest {
    String id;
    boolean payload;

    public ConfigRequest enable(boolean payload) {
        this.payload = payload;
        return this;
    }

    public ConfigRequest agent(String id) {
        this.id = id;
        return this;
    }
}
