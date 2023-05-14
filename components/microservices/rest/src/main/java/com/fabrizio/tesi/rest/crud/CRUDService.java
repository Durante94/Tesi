package com.fabrizio.tesi.rest.crud;

import java.util.Arrays;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
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

        public ResponseTable<TableResponseDTO> getList(TableRequestDTO filter, boolean canEdit) {
                Page<CRUDEntity> results = repository.findAll(new CRUDSpecification(filter),
                                PageRequest.of(filter.getSelectedPage() - 1, filter.getPageSize()));

                GenericAdapter<CRUDEntity, TableResponseDTO> adapter = new GenericAdapter<>(CRUDEntity.class,
                                TableResponseDTO.class);

                return new ResponseTable<>(Long.valueOf(results.getTotalElements()).intValue(),
                                results.stream().map(e -> {
                                        TableResponseDTO tmp = adapter.enityToDto(e);
                                        tmp.setView(true);
                                        tmp.setEdit(canEdit);
                                        tmp.setDelete(canEdit);
                                        return tmp;
                                }).collect(Collectors.toList()));
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
                        try {
                                List<CRUDEntity> presents = repository.findByNameOrAgentId(dto.getName(),
                                                dto.getAgentId());
                                if (presents.stream().allMatch(entity -> entity.getId().longValue() == dto.getId()))
                                        toSave = new CRUDEntity();
                                else
                                        return ResponseEntity.status(HttpStatus.CONFLICT)
                                                        .body(adapter.enityToDto(presents.stream()
                                                                        .filter(entity -> entity.getId()
                                                                                        .longValue() != dto.getId())
                                                                        .findFirst().get()));
                        } catch (NoSuchElementException e) {
                                toSave = new CRUDEntity();
                        }
                }
                adapter.dtoToEntity(toSave, dto);
                repository.save(toSave);

                return ResponseEntity.ok().build();
        }

        public ResponseEntity<Void> delete(long id) {
                try {
                        repository.deleteById(id);
                        return ResponseEntity.ok().build();
                } catch (EmptyResultDataAccessException e) {
                        return ResponseEntity.notFound().build();
                }
        }

        public ResponseEntity<Void> toggleValue(String prop, boolean value) {
                if (fieldPresence(prop, CRUDEntity.class)) {
                        return repository.updateAllEnable(value) > 0
                                        ? ResponseEntity.ok().build()
                                        : ResponseEntity.notFound().build();
                }
                return ResponseEntity.badRequest().build();
        }

        public ResponseEntity<Void> toggleValue(String prop, boolean value, Long id) {
                if (fieldPresence(prop, CRUDEntity.class)) {
                        return repository.updateOneEnable(value, id) > 0
                                        ? ResponseEntity.ok().build()
                                        : ResponseEntity.notFound().build();
                }
                return ResponseEntity.badRequest().build();
        }

        private boolean fieldPresence(String prop, Class<?> type) {
                return Arrays.asList(type.getDeclaredFields()).stream()
                                .anyMatch(field -> field.getName().equals(prop) && (field.getType() == Boolean.TYPE
                                                || field.getType() == boolean.class));
        }
}
