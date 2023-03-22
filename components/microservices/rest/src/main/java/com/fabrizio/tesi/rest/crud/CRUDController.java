package com.fabrizio.tesi.rest.crud;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fabrizio.tesi.rest.common.dto.ResponseTable;
import com.fabrizio.tesi.rest.crud.dto.TableDTO;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;

@RestController
@RequestMapping(value = { "/api" })
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CRUDController {
    @Autowired
    CRUDService service;

    @GetMapping
    public ResponseTable<TableDTO> listElem() {   
        return service.getList();
    }
}
