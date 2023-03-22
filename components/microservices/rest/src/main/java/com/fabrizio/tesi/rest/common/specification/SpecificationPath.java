package com.fabrizio.tesi.rest.common.specification;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Order;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Root;

import com.fabrizio.tesi.rest.common.dto.RequestTable;

public abstract class SpecificationPath<T, U extends RequestTable> {

    private Map<String, Expression<Object>> entityPaths;
    protected final U filter;

    protected SpecificationPath(U filter) {
        this.entityPaths = new HashMap<>();
        this.filter = filter;
    }

    protected abstract void initPaths(Root<T> root);

    protected void addAllPath(Map<String, Path<Object>> paths) {
        entityPaths.putAll(paths);
    }

    protected Expression<Object> getPath(String property) {
        return entityPaths.get(property);
    }

    protected Expression<?> getPath(String property, Class<?> type) {
        return entityPaths.get(property).as(type);
    }

    protected List<Order> addSorter(CriteriaBuilder cb) {
        List<Order> orderList = new LinkedList<Order>();
        for (Entry<String, String> order : filter.getSort().entrySet()) {
            Expression<Object> pd = getPath(order.getKey());

            if (order.getValue().equals("ascend"))
                orderList.add(cb.asc(pd));
            else if (order.getValue().equals("descend"))
                orderList.add(cb.desc(pd));
        }

        return orderList;
    }
}