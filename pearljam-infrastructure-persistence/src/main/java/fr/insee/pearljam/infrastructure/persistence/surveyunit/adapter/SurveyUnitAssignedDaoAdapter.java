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
            "interviewerLastName", "i.last_name",
            "contactOutcome", "co.type"
    );


    @Override
    public Page<SurveyUnitAssigned> findSurveyUnitsAssigned(
            List<String> campaignIds, String search, Pageable pageable) {

        // Build and execute main query with pagination
        List<SurveyUnitAssigned> content = executeMainQuery(campaignIds, search, pageable);

        // Get total count for pagination metadata
        long total = executeCountQuery(campaignIds, search);

        // Return paginated results
        return new PageImpl<>(content, pageable, total);
    }

    private List<SurveyUnitAssigned> executeMainQuery(
            List<String> campaignIds, String search,  Pageable pageable) {

        String sql = """
        SELECT
          su.id                              AS surveyUnitId,
          su.display_name                    AS surveyUnitDisplayName,
          si.ssech                           AS ssech,
          a.l6                               AS addressL6,
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
        LEFT JOIN contact_outcome co
            ON co.survey_unit_id = su.id
        LEFT JOIN interviewer int
            ON int.id = su.interviewer_id
        WHERE su.campaign_id IN (:campaignIds)
        """ +
                            buildSearchCondition(search) +
                            PaginationHelpers.buildSortClause(pageable, ALLOWED_SORTS) +
                            """
                             LIMIT :limit OFFSET :offset
                             """;

        return jdbc.sql(sql)
                .param("campaignIds", campaignIds)
                .param("search", "%" + (search != null ? search.toLowerCase() : "") + "%")
                .param("limit", pageable.getPageSize())
                .param("offset", pageable.getOffset())
                .query(this::mapToSurveyUnitAssigned)
                .list();


    }

    private long executeCountQuery(List<String> campaignIds, String search) {
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
                             """ + buildSearchCondition(search);

        return jdbc.sql(sql)
                .param("campaignIds", campaignIds)
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
                    LOWER(a.l6) LIKE :search OR
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
                rs.getString("addressL6"),
                rs.getString("currentStateType"),
                rs.getString("closingCauseType")
        );
    }
}