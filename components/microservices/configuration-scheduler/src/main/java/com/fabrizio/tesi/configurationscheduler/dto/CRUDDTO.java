package com.fabrizio.tesi.configurationscheduler.dto;

import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

@FieldDefaults(level = AccessLevel.PRIVATE)
@NoArgsConstructor
@Setter
@EqualsAndHashCode
public class CRUDDTO {
    Long id;
    String name;
    @EqualsAndHashCode.Exclude
    String description;
    String function;
    boolean enable;
    String agentId;
}
