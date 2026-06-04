package fr.insee.pearljam.infrastructure.persistence.shared;

import org.junit.jupiter.api.Test;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class PaginationHelpersTest {

    @Test
    void shouldReturnDefaultSortWhenNoSortProvided() {
        Pageable pageable = PageRequest.of(0, 10);

        String result = PaginationHelpers.buildSortClause(
                pageable,
                Map.of("name", "su.name")
        );

        assertEquals(" ORDER BY su.id ASC ", result);
    }

    @Test
    void shouldReturnAscendingSortClause() {
        Pageable pageable = PageRequest.of(
                0,
                10,
                Sort.by(Sort.Order.asc("name"))
        );

        String result = PaginationHelpers.buildSortClause(
                pageable,
                Map.of("name", "su.name")
        );

        assertEquals(" ORDER BY su.name ASC ", result);
    }

    @Test
    void shouldReturnDescendingSortClause() {
        Pageable pageable = PageRequest.of(
                0,
                10,
                Sort.by(Sort.Order.desc("name"))
        );

        String result = PaginationHelpers.buildSortClause(
                pageable,
                Map.of("name", "su.name")
        );

        assertEquals(" ORDER BY su.name DESC ", result);
    }

    @Test
    void shouldThrowExceptionWhenSortPropertyIsNotAllowed() {
        Pageable pageable = PageRequest.of(
                0,
                10,
                Sort.by(Sort.Order.asc("unknown"))
        );

        Map<String, String> allowedSort = Map.of("name", "su.name");
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> PaginationHelpers.buildSortClause(
                        pageable,
                        allowedSort
                )
        );

        assertEquals("Invalid sort column", exception.getMessage());
    }
}