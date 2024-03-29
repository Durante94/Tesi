package com.fabrizio.tesi.configurationscheduler.entity;

import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

import javax.persistence.*;

@Entity
@Table(name = "input_entities")
@FieldDefaults(level = AccessLevel.PRIVATE)
@NoArgsConstructor
@Getter
public class CRUDEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;
    String name;
    @EqualsAndHashCode.Exclude
    String description;
    double amplitude;
    double frequency;
    String function;
    boolean enable;
    @Column(name = "agent_id")
    String agentId;
}
