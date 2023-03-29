package com.fabrizio.tesi.rest.crud.dto;

import javax.persistence.MappedSuperclass;

import com.fabrizio.tesi.rest.common.dto.RequestTable;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.FieldDefaults;

@FieldDefaults(level = AccessLevel.PRIVATE)
@Data
@AllArgsConstructor
@NoArgsConstructor
@MappedSuperclass
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class TableRequestDTO extends RequestTable {
    String name;
    String description;
    double amplitude;
    double frequency;
    String function;
    boolean enable;
}
