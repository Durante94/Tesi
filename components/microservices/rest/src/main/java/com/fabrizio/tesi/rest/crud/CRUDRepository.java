package com.fabrizio.tesi.rest.crud;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import com.fabrizio.tesi.rest.crud.entity.CRUDEntity;

@Transactional
public interface CRUDRepository extends JpaRepository<CRUDEntity, Long>, JpaSpecificationExecutor<CRUDEntity> {
    List<CRUDEntity> findByNameOrAgentId(String name, String agentId);

    @Query("UPDATE CRUDEntity e SET e.enable=?1")
    @Modifying
    int updateAllEnable(boolean value);

    @Query("UPDATE CRUDEntity e SET e.enable=?1 WHERE e.id=?2")
    @Modifying
    int updateOneEnable(boolean value, Long id);
}
