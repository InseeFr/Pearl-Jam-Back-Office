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

    @Test
    void shouldReturnQuestionnaireStateAscendingSortClause() {
        Pageable pageable = PageRequest.of(
                0,
                10,
                Sort.by(Sort.Order.asc("questionnaireState"))
        );

        Map<String, String> allowedSort = Map.of("questionnaireState", "ls.current_state");
        String result = PaginationHelpers.buildSortClause(pageable, allowedSort);

        String expected = " ORDER BY CASE WHEN ls.current_state = 'ANV' THEN 1 " +
                          "WHEN ls.current_state = 'AOC' THEN 2 " +
                          "WHEN ls.current_state = 'NNS' THEN 3 " +
                          "WHEN ls.current_state = 'PRC' THEN 4 " +
                          "WHEN ls.current_state = 'INS' THEN 5 " +
                          "WHEN ls.current_state = 'APS' THEN 6 " +
                          "WHEN ls.current_state = 'TBR' THEN 7 " +
                          "WHEN ls.current_state = 'CLO' THEN 8 " +
                          "WHEN ls.current_state = 'WFS' THEN 9 " +
                          "WHEN ls.current_state = 'WFT' THEN 10 " +
                          "WHEN ls.current_state = 'FIN' THEN 11 " +
                          "WHEN ls.current_state = 'NVA' THEN 12 " +
                          "WHEN ls.current_state = 'NVM' THEN 13 " +
                          "WHEN ls.current_state = 'VIC' THEN 14 " +
                          "WHEN ls.current_state = 'VIN' THEN 15 " +
                          "ELSE 16 END ASC ";

        assertEquals(expected, result);
    }

    @Test
    void shouldReturnQuestionnaireStateDescendingSortClause() {
        Pageable pageable = PageRequest.of(
                0,
                10,
                Sort.by(Sort.Order.desc("questionnaireState"))
        );

        Map<String, String> allowedSort = Map.of("questionnaireState", "ls.current_state");
        String result = PaginationHelpers.buildSortClause(pageable, allowedSort);

        String expected = " ORDER BY CASE WHEN ls.current_state = 'ANV' THEN 1 " +
                          "WHEN ls.current_state = 'AOC' THEN 2 " +
                          "WHEN ls.current_state = 'NNS' THEN 3 " +
                          "WHEN ls.current_state = 'PRC' THEN 4 " +
                          "WHEN ls.current_state = 'INS' THEN 5 " +
                          "WHEN ls.current_state = 'APS' THEN 6 " +
                          "WHEN ls.current_state = 'TBR' THEN 7 " +
                          "WHEN ls.current_state = 'CLO' THEN 8 " +
                          "WHEN ls.current_state = 'WFS' THEN 9 " +
                          "WHEN ls.current_state = 'WFT' THEN 10 " +
                          "WHEN ls.current_state = 'FIN' THEN 11 " +
                          "WHEN ls.current_state = 'NVA' THEN 12 " +
                          "WHEN ls.current_state = 'NVM' THEN 13 " +
                          "WHEN ls.current_state = 'VIC' THEN 14 " +
                          "WHEN ls.current_state = 'VIN' THEN 15 " +
                          "ELSE 16 END DESC ";

        assertEquals(expected, result);
    }

    @Test
    void shouldReturnClosingCauseAscendingSortClause() {
        Pageable pageable = PageRequest.of(
                0,
                10,
                Sort.by(Sort.Order.asc("closingCause"))
        );

        Map<String, String> allowedSort = Map.of("closingCause", "cc.type");
        String result = PaginationHelpers.buildSortClause(pageable, allowedSort);

        String expected = " ORDER BY CASE WHEN cc.type = 'ROW' THEN 1 " +
                          "WHEN cc.type = 'NPA' THEN 2 " +
                          "WHEN cc.type = 'NPI' THEN 3 " +
                          "WHEN cc.type = 'NPX' THEN 4 " +
                          "ELSE 5 END ASC ";

        assertEquals(expected, result);
    }

    @Test
    void shouldReturnClosingCauseDescendingSortClause() {
        Pageable pageable = PageRequest.of(
                0,
                10,
                Sort.by(Sort.Order.desc("closingCause"))
        );

        Map<String, String> allowedSort = Map.of("closingCause", "cc.type");
        String result = PaginationHelpers.buildSortClause(pageable, allowedSort);

        String expected = " ORDER BY CASE WHEN cc.type = 'ROW' THEN 1 " +
                          "WHEN cc.type = 'NPA' THEN 2 " +
                          "WHEN cc.type = 'NPI' THEN 3 " +
                          "WHEN cc.type = 'NPX' THEN 4 " +
                          "ELSE 5 END DESC ";

        assertEquals(expected, result);
    }
}