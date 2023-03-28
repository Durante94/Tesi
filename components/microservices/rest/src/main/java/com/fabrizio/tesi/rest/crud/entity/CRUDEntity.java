package com.fabrizio.tesi.rest.crud.entity;

import javax.persistence.Entity;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

@Entity
@FieldDefaults(level = AccessLevel.PRIVATE)
@NoArgsConstructor
@Getter
@Setter
public class CRUDEntity {
    long id;
    String name;
    String description;
    double amplitude;
    double frequency;
    String function;
}
