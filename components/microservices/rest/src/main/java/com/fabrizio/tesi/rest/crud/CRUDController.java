package com.fabrizio.tesi.rest.crud;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.fabrizio.tesi.rest.common.dto.ResponseTable;
import com.fabrizio.tesi.rest.crud.dto.TableRequestDTO;
import com.fabrizio.tesi.rest.crud.dto.TableResponseDTO;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping(value = { "/api/crud" })
@FieldDefaults(level = AccessLevel.PRIVATE)
@Slf4j
public class CRUDController {
    @Autowired
    CRUDService service;

    ObjectMapper jsonMapper = new ObjectMapper();

    @GetMapping
    @ResponseBody
    public ResponseTable<TableResponseDTO> listElem(
            @RequestParam(required = false, defaultValue = "{}") String filter) {
        try {
            return service.getList(jsonMapper.readValue(filter, TableRequestDTO.class));
        } catch (JsonProcessingException e) {
            log.error("DESERIALIZZAZIONE: {} in {}", filter, TableRequestDTO.class.getName(), e.getMessage());
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Errore formato richiesta");
        }
    }

    @PostMapping
    @ResponseBody
    public TableResponseDTO saveElem(@RequestBody TableResponseDTO dto) {
        return dto;
    }
}
