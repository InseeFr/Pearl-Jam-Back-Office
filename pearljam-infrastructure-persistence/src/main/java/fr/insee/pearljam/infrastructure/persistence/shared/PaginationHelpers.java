package fr.insee.pearljam.infrastructure.persistence.shared;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.util.Map;


public class PaginationHelpers {

    private PaginationHelpers() {
        /* This utility class should not be instantiated */
    }

    public static String buildSortClause(Pageable pageable, Map<String, String> allowedSort) {

        if (pageable.getSort().isEmpty()) {
            return " ORDER BY su.id ASC ";
        }

        Sort.Order order = pageable.getSort().iterator().next();

        String column = allowedSort.get(order.getProperty());

        if (column == null) {
            throw new IllegalArgumentException("Invalid sort column");
        }

        String direction = order.isDescending() ? "DESC" : "ASC";

        return " ORDER BY " + column + " " + direction + " ";
    }
}
