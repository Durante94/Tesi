package com.fabrizio.tesi.rest.common.dto;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import com.fabrizio.tesi.rest.common.annotation.ToEntityIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.experimental.FieldDefaults;

@FieldDefaults(level = AccessLevel.PRIVATE)
@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(value = { "selectedPage", "pageSize", "sort" }, allowSetters = true)
public class RequestTable implements Serializable {
    int selectedPage = 1;
    int pageSize = 10;
    @NonNull
    Map<String, String> sort = new HashMap<>();
    @ToEntityIgnore
    boolean view;
    @ToEntityIgnore
    boolean edit;
    @ToEntityIgnore
    boolean delete;
}
