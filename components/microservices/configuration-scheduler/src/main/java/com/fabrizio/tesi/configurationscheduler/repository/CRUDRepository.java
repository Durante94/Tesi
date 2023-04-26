package com.fabrizio.tesi.configurationscheduler.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.fabrizio.tesi.configurationscheduler.entity.CRUDEntity;

public interface CRUDRepository extends JpaRepository<CRUDEntity, Long> {

}
