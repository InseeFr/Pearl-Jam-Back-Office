package fr.insee.pearljam.infrastructure.persistence.reporting.adapter;

import fr.insee.pearljam.domain.reporting.port.out.SurveyUnitToReviewRepositoryPort;
import fr.insee.pearljam.domain.reporting.readmodel.SurveyUnitToReview;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Component;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

/**
 * DAO adapter implementation for SurveyUnitToReviewRepositoryPort.
 * Provides native SQL pagination and multi-field search capabilities.
 */
@Component
@RequiredArgsConstructor
public class SurveyUnitToReviewDaoAdapter implements SurveyUnitToReviewRepositoryPort {

    private final JdbcClient jdbc;

    @Override
    public Page<SurveyUnitToReview> findSurveyUnitsToReview(
            List<String> campaignIds, List<String> ouIds, String search, Pageable pageable) {

        // Build and execute main query with pagination
        List<SurveyUnitToReview> content = executeMainQuery(campaignIds, ouIds, search, pageable);

        // Get total count for pagination metadata
        long total = executeCountQuery(campaignIds, ouIds, search);

        // Return paginated results
        return new PageImpl<>(content, pageable, total);
    }

    private List<SurveyUnitToReview> executeMainQuery(
            List<String> campaignIds, List<String> ouIds, String search, Pageable pageable) {

        String searchCondition = buildSearchCondition(search);
        String sortClause = buildSortClause(pageable);

        String sql = """
                             SELECT
                                 su.id AS surveyUnitId,
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
                             """ + searchCondition + """
                             """ + sortClause + """
                             LIMIT :limit OFFSET :offset
                             """;

        return jdbc.sql(sql)
                .param("campaignIds", campaignIds)
                .param("ouIds", ouIds)
                .param("search", "%" + (search != null ? search.toLowerCase() : "") + "%")
                .param("limit", pageable.getPageSize())
                .param("offset", pageable.getOffset())
                .query(this::mapToSurveyUnitToReview)
                .list();
    }

    private long executeCountQuery(List<String> campaignIds, List<String> ouIds, String search) {
        String searchCondition = buildSearchCondition(search);

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
                             """ + searchCondition;

        return jdbc.sql(sql)
                .param("campaignIds", campaignIds)
                .param("ouIds", ouIds)
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
                    LOWER(c.label) LIKE LOWER(:search) OR
                    LOWER(su.id) LIKE LOWER(:search) OR
                    LOWER(CONCAT(i.first_name, ' ', i.last_name)) LIKE LOWER(:search)
                )
                """;
    }

    private String buildSortClause(Pageable pageable) {
        if (pageable.getSort().isEmpty()) {
            return "ORDER BY su.id ASC ";
        }

        StringBuilder sortClause = new StringBuilder("ORDER BY ");
        pageable.getSort().forEach(order -> {
            if (sortClause.length() > 9) {
                sortClause.append(", ");
            }
            sortClause.append(getSortColumn(order.getProperty()))
                    .append(" ")
                    .append(order.getDirection());
        });
        return sortClause.toString() + " ";
    }

    private String getSortColumn(String property) {
        return switch (property.toLowerCase()) {
            case "campaignlabel" -> "c.label";
            case "contactoutcome" -> "co.type";
            case "interviewername" -> "CONCAT(i.first_name, ' ', i.last_name)";
            case "viewed" -> "su.viewed";
            case "lastcomment" -> "lastComment";
            default -> "su.id";
        };
    }

    private SurveyUnitToReview mapToSurveyUnitToReview(ResultSet rs, int rowNum) throws SQLException {
        return new SurveyUnitToReview(
                rs.getString("surveyUnitId"),
                rs.getString("campaignLabel"),
                rs.getString("contact_outcome"),
                rs.getString("interviewerId"),
                (rs.getString("interviewerFirstName") + " " + rs.getString("interviewerLastName")).trim(),
                rs.getBoolean("viewed"),
                rs.getString("lastComment")
        );
    }
}