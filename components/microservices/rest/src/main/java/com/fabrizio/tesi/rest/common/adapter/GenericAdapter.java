package com.fabrizio.tesi.rest.common.adapter;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE)
public class GenericAdapter<T, U> {
    static final Pattern p = Pattern.compile("(get|is|set)(\\w+)");
    Map<String, Method> entityGetters;
    Map<String, Method> dtoGetters;
    Map<String, Method> entitySetters;
    Map<String, Method> dtoSetters;
    Constructor<U> dtoConstructor;

    public GenericAdapter(Class<T> entityType, Class<U> dtoType) {
        entityGetters = new HashMap<>();
        dtoGetters = new HashMap<>();
        entitySetters = new HashMap<>();
        dtoSetters = new HashMap<>();

        populateMaps(dtoType, dtoGetters, dtoSetters);
        populateMaps(entityType, entityGetters, entitySetters);

        try {
            dtoConstructor = dtoType.getDeclaredConstructor();
        } catch (NoSuchMethodException | SecurityException e) {
            log.error("Cannot get {} constructor: {}", dtoType.getSimpleName(), e);
        }
    }

    private void populateMaps(Class<?> type, Map<String, Method> getters, Map<String, Method> setters) {
        for (Method method : type.getDeclaredMethods()) {
            Matcher matcher = p.matcher(method.getName());

            if (matcher.find())
                (matcher.group(1).equals("set") ? setters : getters).put(matcher.group(2), method);
        }
        Class<?> superType = type.getSuperclass();
        if (!superType.equals(Object.class))
            populateMaps(superType, getters, setters);
    }

    public U enityToDto(T entity) {
        U dto;
        try {
            dto = dtoConstructor.newInstance();
        } catch (InstantiationException | IllegalAccessException | IllegalArgumentException
                | InvocationTargetException | NullPointerException e) {
            log.error("Cannot create DTO: {}", e);
            return null;
        }

        for (Entry<String, Method> entry : entityGetters.entrySet()) {
            Object value;
            // RETRIEVE VALUE FROM ENTITY GETTER
            try {
                value = entry.getValue().invoke(entity);
            } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
                log.error("Method {} for {} error:", entry.getValue().getName(), entity.getClass().getSimpleName(), e);
                continue;
            }
            // GET THE CORRESPONDING DTO SETTER
            Method dtoMethod;
            try {
                dtoMethod = dtoSetters.get(entry.getKey());
                dtoMethod.setAccessible(true);
            } catch (NullPointerException e) {
                log.error("No method {} for {} error: {}", "set" + entry.getKey(), dto.getClass().getSimpleName(), e);
                continue;
            }
            // SET THE ENTITY VALUE IN THE DTO
            try {
                dtoMethod.invoke(dto, value);
            } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
                log.error("Method {} for {} cannot set value \"{}\": {}", dtoMethod.getName(),
                        dto.getClass().getSimpleName(), value,
                        e);
                continue;
            }
        }
        return dto;
    }

    public void dtoToEntity(T entity, U dto) {
        for (Entry<String, Method> entry : dtoGetters.entrySet()) {
            Object value;
            // RETRIEVE VALUE FROM DTO GETTER
            try {
                value = entry.getValue().invoke(entity);
            } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
                log.error("Method {} for {} error:", entry.getValue().getName(), entity.getClass().getSimpleName(), e);
                continue;
            }
            // GET THE CORRESPONDING ENTITY SETTER
            Method entityMethod;
            try {
                entityMethod = entitySetters.get(entry.getKey());
                entityMethod.setAccessible(true);
            } catch (NullPointerException e) {
                log.error("No method {} for {} error: {}", "set" + entry.getKey(), dto.getClass().getSimpleName(), e);
                continue;
            }
            // SET THE DTO VALUE IN THE ENTITY
            try {
                entityMethod.invoke(dto, value);
            } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
                log.error("Method {} for {} cannot set value \"{}\": {}", entityMethod.getName(),
                        dto.getClass().getSimpleName(), value,
                        e);
                continue;
            }
        }
    }
}
