package com.fabrizio.tesi.rest.crud.entity;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

@Entity
@Table(name = "input_entities")
@FieldDefaults(level = AccessLevel.PRIVATE)
@NoArgsConstructor
@Getter
@Setter
public class CRUDEntity {
    @Id
    Long id;
    String name;
    String description;
    double amplitude;
    double frequency;
    String function;
    boolean enable;
}
