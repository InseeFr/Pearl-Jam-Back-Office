package fr.insee.pearljam.infrastructure.persistence.shared;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.util.Map;


public class PaginationHelpers {

    public static final String ORDER_BY = " ORDER BY ";

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

        // Handle custom sorting for specific fields
        String property = order.getProperty();
        if ("questionnaireState".equals(property)) {
            return buildQuestionnaireStateSortClause(column, direction);
        }
        if ("closingCause".equals(property)) {
            return buildClosingCauseSortClause(column, direction);
        }

        return ORDER_BY + column + " " + direction + " ";
    }

    private static String buildQuestionnaireStateSortClause(String column, String direction) {
        // Order based on alphabetical order of labels:
        // ANV=1, AOC=2, NNS=3, PRC=4, INS=5, APS=6, TBR=7, CLO=8, WFS=9, WFT=10, FIN=11, NVA=12, NVM=13, VIC=14, VIN=15
        // ASC: values ordered by priority 1-15
        // DESC: values ordered by priority 15-1 (SQL DESC on the CASE expression)
        String caseExpression = "CASE WHEN " + column + " = 'ANV' THEN 1 " +
                               "WHEN " + column + " = 'AOC' THEN 2 " +
                               "WHEN " + column + " = 'NNS' THEN 3 " +
                               "WHEN " + column + " = 'PRC' THEN 4 " +
                               "WHEN " + column + " = 'INS' THEN 5 " +
                               "WHEN " + column + " = 'APS' THEN 6 " +
                               "WHEN " + column + " = 'TBR' THEN 7 " +
                               "WHEN " + column + " = 'CLO' THEN 8 " +
                               "WHEN " + column + " = 'WFS' THEN 9 " +
                               "WHEN " + column + " = 'WFT' THEN 10 " +
                               "WHEN " + column + " = 'FIN' THEN 11 " +
                               "WHEN " + column + " = 'NVA' THEN 12 " +
                               "WHEN " + column + " = 'NVM' THEN 13 " +
                               "WHEN " + column + " = 'VIC' THEN 14 " +
                               "WHEN " + column + " = 'VIN' THEN 15 " +
                               "ELSE 16 END";
        return ORDER_BY + caseExpression + " " + direction + " ";
    }

    private static String buildClosingCauseSortClause(String column, String direction) {
        // Order based on alphabetical order of labels:
        // ROW=1, NPA=2, NPI=3, NPX=4
        // ASC: values ordered by priority 1-4
        // DESC: values ordered by priority 4-1 (SQL DESC on the CASE expression)
        String caseExpression = "CASE WHEN " + column + " = 'ROW' THEN 1 " +
                               "WHEN " + column + " = 'NPA' THEN 2 " +
                               "WHEN " + column + " = 'NPI' THEN 3 " +
                               "WHEN " + column + " = 'NPX' THEN 4 " +
                               "ELSE 5 END";
        return ORDER_BY + caseExpression + " " + direction + " ";
    }
}
