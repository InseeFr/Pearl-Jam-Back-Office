package fr.insee.pearljam.infrastructure.persistence.surveyunit.adapter;


import fr.insee.pearljam.domain.surveyunit.port.out.SurveyUnitAssignedRepositoryPort;
import fr.insee.pearljam.domain.surveyunit.readmodel.SurveyUnitAssigned;
import fr.insee.pearljam.infrastructure.persistence.shared.PaginationHelpers;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Component;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

/**
 * DAO adapter implementation for SurveyUnitAssignedRepositoryPort.
 * Provides native SQL pagination and multi-field search capabilities.
 */
@Component
@RequiredArgsConstructor
public class SurveyUnitAssignedDaoAdapter implements SurveyUnitAssignedRepositoryPort {

    private final JdbcClient jdbc;

    private static final Map<String, String> ALLOWED_SORTS = Map.of(
        "surveyUnitId", "su.id",
        "surveyUnitDisplayName", "su.display_name",
        "interviewerLabel", "int.last_name",
        "ssech", "si.ssech",
        "location", "department",
        "city", "city",
        "questionnaireState", "ls.current_state",
        "closingCause", "cc.type"
    );


    @Override
    public Page<SurveyUnitAssigned> findSurveyUnitsAssigned(
        List<String> campaignIds,List<String> lstOuIds, String search, Pageable pageable) {

        // Build and execute main query with pagination
        List<SurveyUnitAssigned> content = executeMainQuery(campaignIds, lstOuIds, search, pageable);

        // Get total count for pagination metadata
        long total = executeCountQuery(campaignIds, lstOuIds, search);

        // Return paginated results
        return new PageImpl<>(content, pageable, total);
    }

    private List<SurveyUnitAssigned> executeMainQuery(
        List<String> campaignIds, List<String> lstOuIds, String search, Pageable pageable) {

        String sql = """
                         SELECT
                           su.id                              AS surveyUnitId,
                           su.display_name                    AS surveyUnitDisplayName,
                           si.ssech                           AS ssech,
                             CASE
                                 WHEN a.l6 ~ '^\\d{5}\\s+' THEN substring(a.l6 from '^(\\d{2})')
                                 ELSE NULL
                             END AS department,
                             CASE
                                 WHEN a.l6 ~ '^\\d{5}\\s+' THEN substring(a.l6 from '^\\d{5}\\s+(.*)$')
                                 WHEN trim(coalesce(a.l6, '')) <> '' THEN trim(a.l6)
                                 ELSE NULL
                             END AS city,
                           ls.current_state                   AS currentStateType,
                           cc.type                            AS closingCauseType,
                          int.first_name                      AS interviewerFirstName,
                          int.last_name                       AS interviewerLastName
                         FROM survey_unit su
                         JOIN LATERAL (
                           SELECT s.type AS current_state
                           FROM state s
                           WHERE s.survey_unit_id = su.id
                           ORDER BY s.date DESC
                           LIMIT 1
                         ) ls ON TRUE
                         LEFT JOIN address a
                             ON a.id = su.address_id
                         LEFT JOIN sample_identifier si
                             ON si.id = su.sample_identifier_id
                         LEFT JOIN closing_cause cc
                             ON cc.survey_unit_id = su.id
                             AND ls.current_state NOT IN ('CLO', 'FIN')
                         LEFT JOIN contact_outcome co
                             ON co.survey_unit_id = su.id
                         LEFT JOIN interviewer int
                             ON int.id = su.interviewer_id
                         WHERE su.campaign_id IN (:campaignIds)
                            AND su.organization_unit_id in (:ouIds)
                         """ +
                     buildSearchCondition(search) +
                     PaginationHelpers.buildSortClause(pageable, ALLOWED_SORTS) +
                     """
                         LIMIT :limit OFFSET :offset
                         """;

        return jdbc.sql(sql)
            .param("campaignIds", campaignIds)
            .param("ouIds", lstOuIds)
            .param("search", "%" + (search != null ? search.toLowerCase() : "") + "%")
            .param("limit", pageable.getPageSize())
            .param("offset", pageable.getOffset())
            .query(this::mapToSurveyUnitAssigned)
            .list();


    }

    private long executeCountQuery(List<String> campaignIds, List<String> lstOuIds, String search) {
        String sql = """
                         SELECT COUNT(DISTINCT su.id)
                            FROM survey_unit su
                            JOIN LATERAL (
                              SELECT s.type AS current_state
                              FROM state s
                              WHERE s.survey_unit_id = su.id
                              ORDER BY s.date DESC
                              LIMIT 1
                            ) ls ON TRUE
                            LEFT JOIN address a
                                ON a.id = su.address_id
                            LEFT JOIN sample_identifier si
                                ON si.id = su.sample_identifier_id
                            LEFT JOIN closing_cause cc
                                ON cc.survey_unit_id = su.id
                            LEFT JOIN contact_outcome co
                                ON co.survey_unit_id = su.id
                            LEFT JOIN interviewer int
                                ON int.id = su.interviewer_id
                            WHERE su.campaign_id IN (:campaignIds)
                            AND su.organization_unit_id in (:ouIds)
                         """ + buildSearchCondition(search);

        return jdbc.sql(sql)
            .param("campaignIds", campaignIds)
            .param("ouIds", lstOuIds)
            .param("search", "%" + (search != null ? search.toLowerCase() : "") + "%")
            .query(Long.class)
            .single();
    }

    private String buildSearchCondition(String search) {
        if (search == null || search.trim().isEmpty()) {
            return "";
        }
        return """
            AND (
                LOWER(COALESCE(a.l6, '')) LIKE :search OR
                LOWER(su.id) LIKE :search OR
                LOWER(CONCAT(int.first_name, ' ', int.last_name)) LIKE :search
            )
            """;
    }

    private SurveyUnitAssigned mapToSurveyUnitAssigned(ResultSet rs, int rowNum) throws SQLException {
        return new SurveyUnitAssigned(
            rs.getString("surveyUnitId"),
            rs.getString("surveyUnitDisplayName"),
            rs.getString("ssech"),
            rs.getString("interviewerFirstName"),
            rs.getString("interviewerLastName"),
            rs.getString("department"),
            rs.getString("city"),
            rs.getString("currentStateType"),
            rs.getString("closingCauseType")
        );
    }
}