package com.fabrizio.tesi.rest.agent.dto;

import com.fabrizio.tesi.rest.common.dto.RequestTable;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

@FieldDefaults(level = AccessLevel.PRIVATE)
@NoArgsConstructor
@Getter
@Setter
public class AgentRequestFilter extends RequestTable {
    String filter = "";

    public boolean applyFiter(String value) {
        return filter == null || value.toLowerCase().contains(filter.toLowerCase());
    }
}
