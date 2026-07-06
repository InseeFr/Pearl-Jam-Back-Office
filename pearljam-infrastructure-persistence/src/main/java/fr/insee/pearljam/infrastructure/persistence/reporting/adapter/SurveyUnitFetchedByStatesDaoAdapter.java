package fr.insee.pearljam.infrastructure.persistence.reporting.adapter;

import fr.insee.pearljam.domain.surveyunit.model.StateType;
import fr.insee.pearljam.domain.surveyunit.port.out.SurveyUnitFetchedByStatesRepositoryPort;
import fr.insee.pearljam.domain.surveyunit.readmodel.SurveyUnitFetchedByStatesAndCampaignIdView;
import fr.insee.pearljam.infrastructure.persistence.shared.PaginationHelpers;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

@Repository
@RequiredArgsConstructor
public class SurveyUnitFetchedByStatesDaoAdapter implements SurveyUnitFetchedByStatesRepositoryPort {

    private final JdbcClient jdbc;

    private static final Map<String, String> ALLOWED_SORTS = Map.of(
            "surveyUnitId",          "su.id",
            "surveyUnitDisplayName", "su.display_name",
            "interviewerLastName",   "int.last_name",
            "contactOutcome",        "co.type",
            "closingCauseType",      "cc.type",
            "endDate",               "ls.last_state_date"
    );

    private static final String BASE_FROM = """
            FROM survey_unit su
            JOIN LATERAL (
                 SELECT s.type AS current_state,
                        s.date AS last_state_date
                FROM state s
                WHERE s.survey_unit_id = su.id
                ORDER BY s.date DESC
                LIMIT 1
            ) ls ON ls.current_state IN (:stateTypes)
            LEFT JOIN interviewer int
                ON int.id = su.interviewer_id
            LEFT JOIN contact_outcome co
                ON co.survey_unit_id = su.id
            LEFT JOIN closing_cause cc
                ON cc.survey_unit_id = su.id
            LEFT JOIN LATERAL (
                SELECT c.value
                FROM comment c
                WHERE c.survey_unit_id = su.id
                  AND c.type = 'MANAGEMENT'
                LIMIT 1
            ) com ON TRUE
            LEFT JOIN visibility vi
                ON vi.campaign_id = su.campaign_id
               AND vi.organization_unit_id = su.organization_unit_id
            WHERE su.campaign_id = :campaignId
              AND su.organization_unit_id IN (:ouIds)
            """;

    private static final String SEARCH_CONDITION = """
        AND (
            LOWER(su.id)                                    LIKE :search OR
            LOWER(int.first_name)                           LIKE :search OR
            LOWER(int.last_name)                            LIKE :search OR
            LOWER(CONCAT(int.first_name,' ',int.last_name)) LIKE :search OR
            LOWER(co.type)                                  LIKE :search OR
            LOWER(cc.type)                                  LIKE :search
        )
        """;

    private static final String MAIN_SELECT = """
        SELECT
            su.id                              AS surveyUnitId,
            su.display_name                    AS surveyUnitDisplayName,
            int.first_name                     AS interviewerFirstName,
            int.last_name                      AS interviewerLastName,
            int.id                             AS interviewerId,
            CAST(ls.last_state_date AS TEXT)   AS endDate,
            co.type                            AS contactOutcome,
            cc.type                            AS closingCauseType,
            su.viewed                          AS viewed,
            com.value                          AS comment
        """;

    @Override
    public Page<SurveyUnitFetchedByStatesAndCampaignIdView> getSurveyUnitsByStatesAndCampaignId(
            List<StateType> stateTypes, String campaignId, String search,
            List<String> ouIds, Pageable pageable) {

        List<String> stateTypesStringified = stateTypes.stream().map(StateType::toString).toList();

        List<SurveyUnitFetchedByStatesAndCampaignIdView> content =
                executeMainQuery(stateTypesStringified, campaignId, search, ouIds, pageable);

        long total = executeCountQuery(stateTypesStringified, campaignId, search, ouIds);

        return new PageImpl<>(content, pageable, total);
    }

    private List<SurveyUnitFetchedByStatesAndCampaignIdView> executeMainQuery(
            List<String> stateTypes, String campaignId, String search,
            List<String> ouIds, Pageable pageable) {

        String sortClause = PaginationHelpers.buildSortClause(pageable, ALLOWED_SORTS);
        String sql = MAIN_SELECT
                + BASE_FROM
                + (hasSearch(search) ? SEARCH_CONDITION : "")
                + sortClause;


        if (pageable.isPaged()) {
            sql += " LIMIT :limit OFFSET :offset";
        }

        return bindCommonParams(jdbc.sql(sql), stateTypes, campaignId, search, ouIds, pageable)
                .query(this::mapRow)
                .list();
    }

    private long executeCountQuery(
            List<String> stateTypes, String campaignId, String search,
            List<String> ouIds) {

        String sql = "SELECT COUNT(DISTINCT su.id) "
                + BASE_FROM
                + (hasSearch(search) ? SEARCH_CONDITION : "");

        return bindCommonParams(jdbc.sql(sql), stateTypes, campaignId, search, ouIds, null)
                .query((rs, _) -> rs.getLong(1))
                .single();
    }

    private static boolean hasSearch(String search) {
        return search != null && !search.isBlank();
    }

    private JdbcClient.StatementSpec bindCommonParams(
            JdbcClient.StatementSpec spec,
            List<String> stateTypes,
            String campaignId,
            String search,
            List<String> ouIds,
            Pageable pageable) {

        spec = spec
                .param("stateTypes", stateTypes)
                .param("campaignId", campaignId)
                .param("ouIds", ouIds)
                .param("search", "%" + (search != null ? search.toLowerCase() : "") + "%");


        if (pageable!= null && pageable.isPaged()) {
            spec = spec
                    .param("limit", pageable.getPageSize())
                    .param("offset", pageable.getOffset());
        }

        return spec;
    }

    private SurveyUnitFetchedByStatesAndCampaignIdView mapRow(ResultSet rs, int rowNum) throws SQLException {
        return new SurveyUnitFetchedByStatesAndCampaignIdView(
                rs.getString("surveyUnitId"),
                rs.getString("surveyUnitDisplayName"),
                rs.getString("interviewerFirstName"),
                rs.getString("interviewerLastName"),
                rs.getString("interviewerId"),
                rs.getString("endDate"),
                rs.getString("contactOutcome"),
                rs.getString("closingCauseType"),
                rs.getBoolean("viewed"),
                rs.getString("comment")
        );
    }
}