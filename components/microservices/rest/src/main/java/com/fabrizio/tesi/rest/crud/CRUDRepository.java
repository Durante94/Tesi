package com.fabrizio.tesi.rest.crud;

import org.springframework.data.jpa.repository.JpaRepository;

import com.fabrizio.tesi.rest.crud.entity.CRUDEntity;

public interface CRUDRepository extends JpaRepository<CRUDEntity, Long> {

}
