package com.fabrizio.tesi.rest.common.adapter;

public class GenericAdapter<T, U> {
    public U enityToDto(T entity) {
        U dto = U.getClass().getDeclaredConstructor().newInstance(null);
    }
}
