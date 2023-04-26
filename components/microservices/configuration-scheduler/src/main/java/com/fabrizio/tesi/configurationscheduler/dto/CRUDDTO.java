package com.fabrizio.tesi.configurationscheduler.dto;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

@FieldDefaults(level = AccessLevel.PRIVATE)
@AllArgsConstructor
@NoArgsConstructor
@Data
public class CRUDDTO {
    Long id;
    String name;
    @EqualsAndHashCode.Exclude
    String description;
    double amplitude;
    double frequency;
    String function;
    boolean enable;
    String agentId;
}
