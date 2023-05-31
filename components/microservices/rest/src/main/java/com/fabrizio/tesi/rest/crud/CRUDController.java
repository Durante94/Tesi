package com.fabrizio.tesi.rest.crud;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
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
            @RequestParam(required = false, defaultValue = "{}") String filter,
            @RequestHeader Map<String, String> headers) {
        try {
            return service.getList(jsonMapper.readValue(filter, TableRequestDTO.class),
                    headers.getOrDefault("role", "").equalsIgnoreCase("admin"));
        } catch (JsonProcessingException e) {
            log.error("DESERIALIZZAZIONE: {} in {}", filter, TableRequestDTO.class.getName(), e.getMessage());
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Errore formato richiesta");
        }
    }

    @GetMapping("/{id}")
    @ResponseBody
    public TableResponseDTO getElem(@PathVariable("id") long id) {
        return service.get(id);
    }

    @PostMapping
    public ResponseEntity<TableResponseDTO> saveElem(@RequestBody TableResponseDTO dto) {
        return service.saveOrUpdate(dto);
    }

    @PostMapping("/{prop}/{value}")
    public ResponseEntity<Void> toggleValue(@PathVariable("prop") String prop, @PathVariable("value") boolean value,
            @RequestBody(required = false) Map<String, Long> body) {
        long id = body.getOrDefault("id", -1L).longValue();
        return id >= 0 ? service.toggleValue(prop, value, id) : service.toggleValue(prop, value);
    }

    @DeleteMapping("/{id}")
    @ResponseBody
    public ResponseEntity<Void> deleteElem(@PathVariable("id") long id) {
        return service.delete(id);
    }
}
