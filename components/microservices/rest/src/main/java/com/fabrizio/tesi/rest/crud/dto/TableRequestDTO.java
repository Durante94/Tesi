package com.fabrizio.tesi.rest.crud.dto;

import com.fabrizio.tesi.rest.common.dto.RequestTable;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

@FieldDefaults(level = AccessLevel.PRIVATE)
@Data
@AllArgsConstructor
@NoArgsConstructor
public class TableRequestDTO extends RequestTable {
    String name;
    String description;
    double amplitude;
    double frequency;
    String function;
}
