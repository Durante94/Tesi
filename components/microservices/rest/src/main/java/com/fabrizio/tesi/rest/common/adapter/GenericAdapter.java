package com.fabrizio.tesi.rest.common.adapter;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE)
@AllArgsConstructor
public class GenericAdapter<T, U> {
    Class<T> entityType;
    Class<U> dtoType;

    public U enityToDto(T entity) {
        U dto;
        try {
            dto = dtoType.getDeclaredConstructor().newInstance();
        } catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException
                | NoSuchMethodException | SecurityException e) {
            log.error("DTO creation error. Enity {} -> {}", entity, e);
            return null;
        }
        for (Method entityMethod : entityType.getDeclaredMethods()) {
            String methodName = entityMethod.getName();
            if (!methodName.startsWith("get"))
                continue;
            String fieldName = methodName.substring(3);
            Object value;
            // RETRIEVE VALUE FROM ENTITY GETTER
            try {
                value = entityMethod.invoke(entity);
            } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
                log.error("Method {} for {} error: {}", methodName, entityType.getName(), e);
                continue;
            }
            // GET THE CORRESPONDING DTO SETTER
            Method dtoMethod;
            try {
                dtoMethod = dtoType.getDeclaredMethod("set" + fieldName, entityMethod.getReturnType());
            } catch (NoSuchMethodException | SecurityException e) {
                log.error("Method {} for {} error: {}", "set" + fieldName, dtoType.getName(), e);
                continue;
            }
            // SET THE ENTITY VALUE IN THE DTO
            try {
                dtoMethod.invoke(dto, entityMethod.getReturnType().cast(value));
            } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
                log.error("Method {} for {} cannot set value \"{}\": {}", dtoMethod.getName(), dtoType.getName(), value,
                        e);
                continue;
            }
        }
        return dto;
    }
}
