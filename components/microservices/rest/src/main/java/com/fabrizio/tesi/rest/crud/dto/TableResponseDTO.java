package com.fabrizio.tesi.rest.crud.dto;

import com.fabrizio.tesi.rest.common.annotation.ToEntityIgnore;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

@FieldDefaults(level = AccessLevel.PRIVATE)
@Getter
@Setter
@NoArgsConstructor
public class TableResponseDTO extends TableRequestDTO {
    @ToEntityIgnore
    long id;
    boolean readOnly;
}
