package com.fabrizio.tesi.rest.agent.dto;

import com.fabrizio.tesi.rest.common.dto.RequestTable;

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
@EqualsAndHashCode(callSuper = true)
public class AgentRequestFilter extends RequestTable {
    String filter = "";

    public boolean applyFiter(String value) {
        return value.toLowerCase().contains(filter.toLowerCase());
    }
}
