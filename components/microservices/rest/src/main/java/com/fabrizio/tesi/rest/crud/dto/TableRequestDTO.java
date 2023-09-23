package com.fabrizio.tesi.rest.crud.dto;

import com.fabrizio.tesi.rest.common.dto.RequestTable;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

@FieldDefaults(level = AccessLevel.PRIVATE)
@Getter
@Setter
@NoArgsConstructor
public class TableRequestDTO extends RequestTable {
    String name;
    String description;
    String function;
    boolean enable;
    String agentId;
}
