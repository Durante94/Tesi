package com.fabrizio.tesi.rest.crud.specification;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.apache.commons.lang3.StringUtils;
import org.springframework.data.jpa.domain.Specification;

import com.fabrizio.tesi.rest.common.specification.SpecificationPath;
import com.fabrizio.tesi.rest.crud.dto.TableRequestDTO;
import com.fabrizio.tesi.rest.crud.entity.CRUDEntity;

public class CRUDSpecification extends SpecificationPath<CRUDEntity, TableRequestDTO>
        implements Specification<CRUDEntity> {

    public CRUDSpecification(TableRequestDTO filter) {
        super(filter);
    }

    @Override
    public Predicate toPredicate(Root<CRUDEntity> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
        Predicate p = criteriaBuilder.conjunction();

        if (StringUtils.isNotBlank(filter.getName())) {
            p.getExpressions().add(criteriaBuilder.and(
                    criteriaBuilder.like(
                        criteriaBuilder.lower(root.get("name")), 
                        "%" + filter.getName().toLowerCase() + "%"
                    )
            ));
        }
        if (StringUtils.isNotBlank(filter.getDescription())) {
            p.getExpressions().add(criteriaBuilder.and(
                    criteriaBuilder.like(
                        criteriaBuilder.lower(root.get("description")), 
                        "%" + filter.getDescription().toLowerCase() + "%"
                    )
            ));
        }
        if (filter.getAmplitude() != 0) {
            p.getExpressions().add(criteriaBuilder.and(
                criteriaBuilder.like(
                    root.get("amplitude").as(String.class),
                    Double.toString(filter.getAmplitude())
                ) 
            ));
        }
        if (filter.getFrequency() != 0) {
            p.getExpressions().add(criteriaBuilder.and(
                criteriaBuilder.like(
                    root.get("frequency").as(String.class),
                    Double.toString(filter.getFrequency())
                ) 
            ));
        }
        if (StringUtils.isNotBlank(filter.getFunction())) {
            p.getExpressions().add(criteriaBuilder.and(
                    criteriaBuilder.like(
                        criteriaBuilder.lower(root.get("function")), 
                        "%" + filter.getFunction().toLowerCase() + "%"
                    )
            ));
        }

        query.orderBy(addSorter(criteriaBuilder));

        return p;
    }
}
