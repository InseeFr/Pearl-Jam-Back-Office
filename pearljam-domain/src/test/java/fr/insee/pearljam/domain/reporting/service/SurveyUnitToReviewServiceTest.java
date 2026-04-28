package fr.insee.pearljam.domain.reporting.service;

import fr.insee.pearljam.domain.campaign.port.in.DateService;
import fr.insee.pearljam.domain.campaign.port.out.CampaignVisibilityPort;
import fr.insee.pearljam.domain.campaign.readmodel.CampaignVisibility;
import fr.insee.pearljam.domain.campaign.service.dummy.FixedDateService;
import fr.insee.pearljam.domain.organizationunit.port.in.UserService;
import fr.insee.pearljam.domain.organizationunit.readmodel.OrganizationUnitSummary;
import fr.insee.pearljam.domain.reporting.port.in.SurveyUnitToReviewStatsPresenter;
import fr.insee.pearljam.domain.reporting.port.out.SurveyUnitToReviewRepositoryPort;
import fr.insee.pearljam.domain.reporting.readmodel.SurveyUnitToReview;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
class SurveyUnitToReviewServiceTest {


    UserService userService;
    CampaignVisibilityPort campaignVisibilityPort;
    SurveyUnitToReviewRepositoryPort surveyUnitToReviewRepositoryPort;
    DateService dateService;
    SurveyUnitToReviewStatsPresenter<Object> presenter;
    SurveyUnitToReviewService service;
    static final String USER_ID = "user-1";


    @BeforeEach
    void setup() {
        campaignVisibilityPort = mock(CampaignVisibilityPort.class);
        userService = mock(UserService.class);
        dateService = new FixedDateService();
        surveyUnitToReviewRepositoryPort = mock(SurveyUnitToReviewRepositoryPort.class);
        service = new SurveyUnitToReviewService(surveyUnitToReviewRepositoryPort, userService, campaignVisibilityPort, dateService);
        when(userService.getUserOUsModel(USER_ID, true)).thenReturn(List.of(new OrganizationUnitSummary("OU1", "Organization-Unit-1")));
    }

    @Test
    void shouldExtractCampaignIdsAndCallRepositoryWithCorrectArguments() {
        // GIVEN
        Pageable pageable = PageRequest.of(0, 10);
        String search = "abc";
        long now = dateService.getCurrentTimestamp();

        List<String> expectedOuIds = List.of("OU1");

        List<CampaignVisibility> campaigns = List.of(
                toCampaignVisibility("C1"),
                toCampaignVisibility("C2")
        );

        when(campaignVisibilityPort
                .findCampaignsWithVisibilityByUserAndManagementVisibility(
                        expectedOuIds, USER_ID, now))
                .thenReturn(campaigns);

        when(surveyUnitToReviewRepositoryPort.findSurveyUnitsToReview(
                anyList(), anyList(), any(), any()))
                .thenReturn(Page.empty());

        presenter = mock(SurveyUnitToReviewStatsPresenter.class);
        when(presenter.present(any())).thenReturn(new Object());

        // WHEN
        service.getSurveyUnitsToReview(USER_ID, search, pageable, presenter);

        // THEN
        ArgumentCaptor<List<String>> campaignIdsCaptor = ArgumentCaptor.forClass(List.class);
        ArgumentCaptor<List<String>> ouIdsCaptor = ArgumentCaptor.forClass(List.class);

        verify(surveyUnitToReviewRepositoryPort).findSurveyUnitsToReview(
                campaignIdsCaptor.capture(),
                ouIdsCaptor.capture(),
                eq(search),
                eq(pageable)
        );

        assertEquals(List.of("C1", "C2"), campaignIdsCaptor.getValue());
        assertEquals(expectedOuIds, ouIdsCaptor.getValue());
    }


    @Test
    void shouldHandleUserWithNoOrganizationUnits() {
        // GIVEN
        Pageable pageable = PageRequest.of(0, 10);
        long now = dateService.getCurrentTimestamp();

        when(userService.getUserOUsModel(USER_ID, true))
                .thenReturn(List.of()); // 👈 aucun OU

        when(campaignVisibilityPort
                .findCampaignsWithVisibilityByUserAndManagementVisibility(
                        List.of(), USER_ID, now))
                .thenReturn(List.of());

        when(surveyUnitToReviewRepositoryPort.findSurveyUnitsToReview(
                anyList(), anyList(), any(), any()))
                .thenReturn(Page.empty());

        presenter = mock(SurveyUnitToReviewStatsPresenter.class);
        when(presenter.present(any())).thenReturn(new Object());

        // WHEN
        service.getSurveyUnitsToReview(USER_ID, null, pageable, presenter);

        // THEN
        verify(surveyUnitToReviewRepositoryPort).findSurveyUnitsToReview(
                List.of(),
                List.of(),
                null,
                pageable
        );
    }

    @Test
    void shouldHandleNullSearch() {
        // GIVEN
        Pageable pageable = PageRequest.of(0, 10);
        long now = dateService.getCurrentTimestamp();

        when(campaignVisibilityPort
                .findCampaignsWithVisibilityByUserAndManagementVisibility(
                        anyList(), eq(USER_ID), eq(now)))
                .thenReturn(List.of(toCampaignVisibility("C1")));

        when(surveyUnitToReviewRepositoryPort.findSurveyUnitsToReview(
                anyList(), anyList(), isNull(), any()))
                .thenReturn(Page.empty());

        presenter = mock(SurveyUnitToReviewStatsPresenter.class);
        when(presenter.present(any())).thenReturn(new Object());

        // WHEN
        service.getSurveyUnitsToReview(USER_ID, null, pageable, presenter);

        // THEN
        verify(surveyUnitToReviewRepositoryPort).findSurveyUnitsToReview(
                List.of("C1"),
                List.of("OU1"),
                null,
                pageable
        );
    }

    @Test
    void shouldHandleEmptySearch() {
        // GIVEN
        Pageable pageable = PageRequest.of(0, 10);
        String search = "";
        long now = dateService.getCurrentTimestamp();

        when(campaignVisibilityPort
                .findCampaignsWithVisibilityByUserAndManagementVisibility(
                        anyList(), eq(USER_ID), eq(now)))
                .thenReturn(List.of(toCampaignVisibility("C1")));

        when(surveyUnitToReviewRepositoryPort.findSurveyUnitsToReview(
                anyList(), anyList(), eq(""), any()))
                .thenReturn(Page.empty());

        presenter = mock(SurveyUnitToReviewStatsPresenter.class);
        when(presenter.present(any())).thenReturn(new Object());

        // WHEN
        service.getSurveyUnitsToReview(USER_ID, search, pageable, presenter);

        // THEN
        verify(surveyUnitToReviewRepositoryPort).findSurveyUnitsToReview(
                List.of("C1"),
                List.of("OU1"),
                "",
                pageable
        );
    }

    @Test
    void shouldPassCorrectPagination() {
        // GIVEN
        Pageable pageable = PageRequest.of(2, 20);
        long now = dateService.getCurrentTimestamp();

        when(campaignVisibilityPort
                .findCampaignsWithVisibilityByUserAndManagementVisibility(
                        anyList(), eq(USER_ID), eq(now)))
                .thenReturn(List.of(toCampaignVisibility("C1")));

        when(surveyUnitToReviewRepositoryPort.findSurveyUnitsToReview(
                anyList(), anyList(), any(), eq(pageable)))
                .thenReturn(Page.empty());

        presenter = mock(SurveyUnitToReviewStatsPresenter.class);
        when(presenter.present(any())).thenReturn(new Object());

        // WHEN
        service.getSurveyUnitsToReview(USER_ID, null, pageable, presenter);

        // THEN
        verify(surveyUnitToReviewRepositoryPort).findSurveyUnitsToReview(
                List.of("C1"),
                List.of("OU1"),
                null,
                pageable
        );
    }

    @Test
    void shouldHandlePaginationWithTotalElements() {
        // GIVEN
        Pageable pageable = PageRequest.of(1, 2); // page 1 (2e page)
        String search = "abc";
        long now = dateService.getCurrentTimestamp();

        when(campaignVisibilityPort
                .findCampaignsWithVisibilityByUserAndManagementVisibility(
                        List.of("OU1"), USER_ID, now))
                .thenReturn(List.of(toCampaignVisibility("C1")));

        List<SurveyUnitToReview> content = List.of(
                mock(SurveyUnitToReview.class),
                mock(SurveyUnitToReview.class)
        );

        Page<SurveyUnitToReview> page =
                new PageImpl<>(content, pageable, 5); // 👈 TOTAL = 5

        when(surveyUnitToReviewRepositoryPort.findSurveyUnitsToReview(
                anyList(), anyList(), eq(search), eq(pageable)))
                .thenReturn(page);

        presenter = mock(SurveyUnitToReviewStatsPresenter.class);
        when(presenter.present(page)).thenReturn("RESULT");

        // WHEN
        Object result = service.getSurveyUnitsToReview(USER_ID, search, pageable, presenter);

        // THEN
        assertEquals("RESULT", result);

        verify(presenter).present(argThat(p ->
                p.getTotalElements() == 5 &&
                p.getNumber() == 1 &&
                p.getSize() == 2 &&
                p.getContent().size() == 2
        ));
    }


private CampaignVisibility toCampaignVisibility(String id) {
    return new CampaignVisibility(
            id,
            "label-" + id,
            "test@mail.com",
            1L, 2L, 3L, 4L, 5L, 6L
    );
}}


