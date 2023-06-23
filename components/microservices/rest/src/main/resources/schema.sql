-- MySQL dump 10.13  Distrib 8.0.21, for Linux (x86_64)

CREATE TABLE IF NOT EXISTS input_entities (
    Id bigint NOT NULL AUTO_INCREMENT,
    name varchar(50) NOT NULL,
    description varchar(100) DEFAULT NULL,
    amplitude decimal(6,3) NOT NULL DEFAULT 0,
    frequency decimal(6,3) NOT NULL DEFAULT 0,
    function varchar(10) NOT NULL,
    enable tinyint(1) NOT NULL DEFAULT 0,
    agent_id varchar(100) NOT NULL 
);