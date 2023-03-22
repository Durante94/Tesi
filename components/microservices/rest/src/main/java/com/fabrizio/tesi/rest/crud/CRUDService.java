package com.fabrizio.tesi.rest.crud;

import java.util.stream.Collector;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import com.fabrizio.tesi.rest.common.adapter.GenericAdapter;
import com.fabrizio.tesi.rest.common.dto.ResponseTable;
import com.fabrizio.tesi.rest.crud.dto.TableRequestDTO;
import com.fabrizio.tesi.rest.crud.dto.TableResponseDTO;
import com.fabrizio.tesi.rest.crud.entity.CRUDEntity;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;

@Service
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CRUDService {
    @Autowired
    CRUDRepository repository;

    public ResponseTable<TableResponseDTO> getList(TableRequestDTO filter) {
        Page<CRUDEntity> results = repository.findAll(null,
                PageRequest.of(filter.getSelectedPage() - 1, filter.getPageSize()));
        GenericAdapter<CRUDEntity, TableResponseDTO> adapter = new GenericAdapter<>();
        return new ResponseTable<>(Long.valueOf(results.getTotalElements()).intValue(),
                results.stream().map(e -> adapter.enityToDto(e)).collect(Collectors.toList()));
    }
}
