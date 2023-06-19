package com.fabrizio.tesi.rest.common.dto;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import com.fabrizio.tesi.rest.common.annotation.ToEntityIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

@FieldDefaults(level = AccessLevel.PRIVATE)
@Getter
@NoArgsConstructor
@JsonIgnoreProperties(value = { "selectedPage", "pageSize", "sort" }, allowSetters = true)
public class RequestTable implements Serializable {
	int selectedPage = 1;
	int pageSize = 10;
	@NonNull
	Map<String, String> sort = new HashMap<>();
	@Setter
	@ToEntityIgnore
	boolean view;
	@Setter
	@ToEntityIgnore
	boolean edit;
	@Setter
	@ToEntityIgnore
	boolean delete;
}
