package fr.insee.pearljam.infrastructure.persistence.reporting.adapter;

import fr.insee.pearljam.domain.reporting.port.out.SurveyUnitToReviewRepositoryPort;
import fr.insee.pearljam.domain.surveyunit.service.model.SurveyUnitToReview;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Component;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.List;
import java.util.Map;

/**
 * DAO adapter implementation for SurveyUnitToReviewRepositoryPort.
 * Provides native SQL pagination and multi-field search capabilities.
 */
@Component
@RequiredArgsConstructor
public class SurveyUnitToReviewDaoAdapter implements SurveyUnitToReviewRepositoryPort {

    private final JdbcClient jdbc;

    private static final Map<String, String> ALLOWED_SORTS = Map.of(
            "campaignLabel", "c.label",
            "surveyUnitId", "su.id",
            "interviewerLastName", "i.last_name",
            "contactOutcome", "co.type"
    );


    @Override
    public Page<SurveyUnitToReview> findSurveyUnitsToReview(
            List<String> campaignIds, List<String> ouIds, String search, Boolean viewed, Pageable pageable) {

        // Build and execute main query with pagination
        List<SurveyUnitToReview> content = executeMainQuery(campaignIds, ouIds, search, viewed, pageable);

        // Get total count for pagination metadata
        long total = executeCountQuery(campaignIds, ouIds, search, viewed);


        // Return paginated results
        return new PageImpl<>(content, pageable, total);
    }

    private List<SurveyUnitToReview> executeMainQuery(
            List<String> campaignIds, List<String> ouIds, String search, Boolean viewed, Pageable pageable) {

        String sql = """
                             SELECT
                                 su.id AS surveyUnitId,
                                 su.display_name AS surveyUnitDisplayName,
                                 c.label AS campaignLabel,
                                 co.type AS contact_outcome,
                                 i.id AS interviewerId,
                                 i.first_name AS interviewerFirstName,
                                 i.last_name AS interviewerLastName,
                                 su.viewed,
                                 c.id AS campaignId,
                                 (
                                     SELECT c2.value
                                     FROM comment c2
                                     WHERE c2.survey_unit_id = su.id
                                       AND c2.type = 'MANAGEMENT'
                                     LIMIT 1
                                 ) AS lastComment
                             FROM survey_unit su
                             JOIN campaign c
                                 ON su.campaign_id = c.id
                             LEFT JOIN interviewer i
                                 ON su.interviewer_id = i.id
                             LEFT JOIN contact_outcome co
                                 ON co.survey_unit_id = su.id
                             JOIN LATERAL (
                                 SELECT s.type AS current_state
                                 FROM state s
                                 WHERE s.survey_unit_id = su.id
                                 ORDER BY s.date DESC
                                 LIMIT 1
                             ) ls
                                 ON TRUE
                             WHERE ls.current_state = 'TBR'
                               AND su.campaign_id IN (:campaignIds)
                               AND su.organization_unit_id IN (:ouIds)
                               AND (:viewed IS NULL OR su.viewed =:viewed)
                             """ +
                            buildSearchCondition(search) +
                            buildSortClause(pageable) +
                            """
                             LIMIT :limit OFFSET :offset
                             """;

        return jdbc.sql(sql)
                .param("campaignIds", campaignIds)
                .param("ouIds", ouIds)
                .param("search", "%" + (search != null ? search.toLowerCase() : "") + "%")
                .param("viewed", viewed, Types.BOOLEAN)
                .param("limit", pageable.getPageSize())
                .param("offset", pageable.getOffset())
                .query(this::mapToSurveyUnitToReview)
                .list();


    }

    private long executeCountQuery(List<String> campaignIds, List<String> ouIds, String search, Boolean viewed) {
        String sql = """
                             SELECT COUNT(DISTINCT su.id)
                             FROM survey_unit su
                             JOIN campaign c ON su.campaign_id = c.id
                             LEFT JOIN contact_outcome co ON co.survey_unit_id = su.id
                             LEFT JOIN interviewer i ON su.interviewer_id = i.id
                             JOIN LATERAL (
                                 SELECT s.type AS current_state
                                 FROM state s
                                 WHERE s.survey_unit_id = su.id
                                 ORDER BY s.date DESC
                                 LIMIT 1
                             ) ls
                                 ON TRUE
                             WHERE ls.current_state = 'TBR'
                                AND su.campaign_id IN (:campaignIds)
                                AND su.organization_unit_id IN (:ouIds)
                                AND (:viewed IS NULL OR su.viewed =:viewed)
                             """ + buildSearchCondition(search);

        return jdbc.sql(sql)
                .param("campaignIds", campaignIds)
                .param("ouIds", ouIds)
                .param("search", "%" + (search != null ? search.toLowerCase() : "") + "%")
                .param("viewed", viewed, Types.BOOLEAN)
                .query(Long.class)
                .single();
    }

    private String buildSearchCondition(String search) {
        if (search == null || search.trim().isEmpty()) {
            return "";
        }
        return """
                AND (
                    LOWER(c.label) LIKE :search OR
                    LOWER(su.id) LIKE :search OR
                    LOWER(CONCAT(i.first_name, ' ', i.last_name)) LIKE :search
                )
                """;
    }



    private String buildSortClause(Pageable pageable) {

        if (pageable.getSort().isEmpty()) {
            return " ORDER BY su.id ASC ";
        }

        Sort.Order order = pageable.getSort().iterator().next();

        String column = ALLOWED_SORTS.get(order.getProperty());

        if (column == null) {
            throw new IllegalArgumentException("Invalid sort column");
        }

        String direction = order.isDescending() ? "DESC" : "ASC";

        return " ORDER BY " + column + " " + direction + " ";
    }

    private SurveyUnitToReview mapToSurveyUnitToReview(ResultSet rs, int rowNum) throws SQLException {
        return new SurveyUnitToReview(
                rs.getString("surveyUnitId"),
                rs.getString("surveyUnitDisplayName"),
                rs.getString("campaignLabel"),
                rs.getString("contact_outcome"),
                rs.getString("interviewerId"),
                rs.getString("interviewerFirstName"),
                rs.getString("interviewerLastName"),
                rs.getBoolean("viewed"),
                rs.getString("lastComment")
        );
    }
}