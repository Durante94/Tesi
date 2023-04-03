package com.fabrizio.tesi.rest.crud;

import java.util.NoSuchElementException;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.fabrizio.tesi.rest.common.adapter.GenericAdapter;
import com.fabrizio.tesi.rest.common.dto.ResponseTable;
import com.fabrizio.tesi.rest.crud.dto.TableRequestDTO;
import com.fabrizio.tesi.rest.crud.dto.TableResponseDTO;
import com.fabrizio.tesi.rest.crud.entity.CRUDEntity;
import com.fabrizio.tesi.rest.crud.specification.CRUDSpecification;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;

@Service
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CRUDService {
        @Autowired
        CRUDRepository repository;

        public ResponseTable<TableResponseDTO> getList(TableRequestDTO filter) {
                Page<CRUDEntity> results = repository.findAll(new CRUDSpecification(filter),
                                PageRequest.of(filter.getSelectedPage() - 1, filter.getPageSize()));

                GenericAdapter<CRUDEntity, TableResponseDTO> adapter = new GenericAdapter<>(CRUDEntity.class,
                                TableResponseDTO.class);

                return new ResponseTable<>(Long.valueOf(results.getTotalElements()).intValue(),
                                results.stream().map(e -> adapter.enityToDto(e)).collect(Collectors.toList()));
        }

        public TableResponseDTO get(long id) {
                GenericAdapter<CRUDEntity, TableResponseDTO> adapter = new GenericAdapter<>(CRUDEntity.class,
                                TableResponseDTO.class);

                CRUDEntity result = repository.findById(id)
                                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
                return adapter.enityToDto(result);
        }

        public ResponseEntity<TableResponseDTO> saveOrUpdate(TableResponseDTO dto) {
                CRUDEntity toSave;
                GenericAdapter<CRUDEntity, TableResponseDTO> adapter = new GenericAdapter<>(CRUDEntity.class,
                                TableResponseDTO.class);
                if (dto.getId() > 0)
                        toSave = repository.findById(dto.getId())
                                        .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
                else {
                        TableRequestDTO filter = new TableRequestDTO(dto.getName(), null, 0,
                                        0, null, false);
                        try {
                                CRUDEntity present = repository.findOne(new CRUDSpecification(filter)).get();
                                return ResponseEntity.status(HttpStatus.CONFLICT).body(adapter.enityToDto(present));
                        } catch (NoSuchElementException e) {
                                toSave = new CRUDEntity();
                        }
                }
                adapter.dtoToEntity(toSave, dto);
                repository.save(toSave);

                return ResponseEntity.ok().build();
        }
}
