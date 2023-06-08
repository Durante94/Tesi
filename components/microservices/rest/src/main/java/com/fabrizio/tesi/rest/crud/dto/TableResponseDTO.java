package com.fabrizio.tesi.rest.crud.dto;

import com.fabrizio.tesi.rest.common.annotation.ToEntityIgnore;

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
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class TableResponseDTO extends TableRequestDTO {
    @ToEntityIgnore
    long id;
    boolean readOnly;
}
