package fr.insee.pearljam.domain.reporting.service;

import fr.insee.pearljam.domain.campaign.port.in.DateService;
import fr.insee.pearljam.domain.campaign.port.out.CampaignVisibilityPort;
import fr.insee.pearljam.domain.campaign.readmodel.CampaignVisibility;
import fr.insee.pearljam.domain.campaign.service.dummy.FixedDateService;
import fr.insee.pearljam.domain.organizationunit.port.in.UserService;
import fr.insee.pearljam.domain.organizationunit.readmodel.OrganizationUnitSummary;
import fr.insee.pearljam.domain.surveyunit.port.in.SurveyUnitToReviewPresenter;
import fr.insee.pearljam.domain.surveyunit.port.out.SurveyUnitToReviewRepositoryPort;
import fr.insee.pearljam.domain.surveyunit.readmodel.SurveyUnitToReview;
import fr.insee.pearljam.domain.surveyunit.service.SurveyUnitToReviewService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
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
    SurveyUnitToReviewPresenter<Object> presenter;
    SurveyUnitToReviewService service;
    static final String USER_ID = "user-1";


    @BeforeEach
    void setup() {
        campaignVisibilityPort = mock(CampaignVisibilityPort.class);
        userService = mock(UserService.class);
        dateService = new FixedDateService();
        surveyUnitToReviewRepositoryPort = mock(SurveyUnitToReviewRepositoryPort.class);
        presenter = mock(SurveyUnitToReviewPresenter.class);

        service = new SurveyUnitToReviewService(surveyUnitToReviewRepositoryPort, userService, campaignVisibilityPort, dateService);
        when(userService.getUserOUsModel(USER_ID, true)).thenReturn(List.of(new OrganizationUnitSummary("OU1", "Organization-Unit-1")));
    }

    @Test
    void shouldExtractCampaignIdsAndCallRepositoryWithCorrectArguments() {
        // GIVEN
        Pageable pageable = PageRequest.of(0, 10);
        String search = "abc";
        String campaignId = "";
        long now = dateService.getCurrentTimestamp();

        List<String> expectedOuIds = List.of("OU1");

        List<CampaignVisibility> campaigns = List.of(
                toCampaignVisibility("C1"),
                toCampaignVisibility("C2")
        );

        when(campaignVisibilityPort
                .findPreferredCampaignsWithVisibilityByUserAndManagementVisibility(
                        expectedOuIds, USER_ID, now))
                .thenReturn(campaigns);

        when(surveyUnitToReviewRepositoryPort.findSurveyUnitsToReview(
                anyList(),
                anyList(),
                any(),
                any(),
                any(Pageable.class)
        )).thenReturn(Page.empty());

        when(presenter.present(any())).thenReturn(new Object());

        // WHEN
        service.getSurveyUnitsToReview(
                USER_ID,
                campaignId,
                search,
                null,
                pageable,
                presenter
        );

        // THEN
        verify(surveyUnitToReviewRepositoryPort).findSurveyUnitsToReview(
                eq(List.of("C1", "C2")),
                eq(expectedOuIds),
                eq(search),
                isNull(),
                eq(pageable)
        );
    }


    @Test
    void shouldHandleUserWithNoOrganizationUnits() {
        Pageable pageable = PageRequest.of(0, 10);
        long now = dateService.getCurrentTimestamp();

        when(userService.getUserOUsModel(USER_ID, true))
                .thenReturn(List.of());

        when(campaignVisibilityPort
                .findPreferredCampaignsWithVisibilityByUserAndManagementVisibility(
                        List.of(), USER_ID, now))
                .thenReturn(List.of());

        when(surveyUnitToReviewRepositoryPort.findSurveyUnitsToReview(
                anyList(), anyList(), any(), any(), any(Pageable.class)
        )).thenReturn(Page.empty());

        when(presenter.present(any())).thenReturn(new Object());

        service.getSurveyUnitsToReview(USER_ID, null, null, null, pageable, presenter);

        verify(surveyUnitToReviewRepositoryPort).findSurveyUnitsToReview(
                eq(List.of()),
                eq(List.of()),
                isNull(),
                isNull(),
                eq(pageable)
        );
    }

    @Test
    void shouldHandleNullSearch() {
        Pageable pageable = PageRequest.of(0, 10);
        long now = dateService.getCurrentTimestamp();

        when(campaignVisibilityPort
                .findPreferredCampaignsWithVisibilityByUserAndManagementVisibility(
                        anyList(), eq(USER_ID), eq(now)))
                .thenReturn(List.of(toCampaignVisibility("C1")));

        when(surveyUnitToReviewRepositoryPort.findSurveyUnitsToReview(
                anyList(), anyList(), any(), any(), any(Pageable.class)
        )).thenReturn(Page.empty());

        when(presenter.present(any())).thenReturn(new Object());

        service.getSurveyUnitsToReview(USER_ID, null, null, true, pageable, presenter);

        verify(surveyUnitToReviewRepositoryPort).findSurveyUnitsToReview(
                eq(List.of("C1")),
                eq(List.of("OU1")),
                isNull(),
                eq(true),
                eq(pageable)
        );
    }

    @Test
    void shouldHandleEmptySearch() {
        Pageable pageable = PageRequest.of(0, 10);
        long now = dateService.getCurrentTimestamp();

        when(campaignVisibilityPort
                .findPreferredCampaignsWithVisibilityByUserAndManagementVisibility(
                        anyList(), eq(USER_ID), eq(now)))
                .thenReturn(List.of(toCampaignVisibility("C1")));

        when(surveyUnitToReviewRepositoryPort.findSurveyUnitsToReview(
                anyList(), anyList(), any(), any(), any(Pageable.class)
        )).thenReturn(Page.empty());

        when(presenter.present(any())).thenReturn(new Object());

        service.getSurveyUnitsToReview(USER_ID, null, "", false, pageable, presenter);

        verify(surveyUnitToReviewRepositoryPort).findSurveyUnitsToReview(
                eq(List.of("C1")),
                eq(List.of("OU1")),
                eq(""),
                eq(false),
                eq(pageable)
        );
    }

    @Test
    void shouldPassCorrectPagination() {
        Pageable pageable = PageRequest.of(2, 20);
        long now = dateService.getCurrentTimestamp();

        when(campaignVisibilityPort
                .findPreferredCampaignsWithVisibilityByUserAndManagementVisibility(
                        anyList(), eq(USER_ID), eq(now)))
                .thenReturn(List.of(toCampaignVisibility("C1")));

        when(surveyUnitToReviewRepositoryPort.findSurveyUnitsToReview(
                anyList(), anyList(), any(), any(), eq(pageable)
        )).thenReturn(Page.empty());

        when(presenter.present(any())).thenReturn(new Object());

        service.getSurveyUnitsToReview(USER_ID, null, null, null, pageable, presenter);

        verify(surveyUnitToReviewRepositoryPort).findSurveyUnitsToReview(
                eq(List.of("C1")),
                eq(List.of("OU1")),
                isNull(),
                isNull(),
                eq(pageable)
        );
    }

    @Test
    void shouldHandlePaginationWithTotalElements() {
        Pageable pageable = PageRequest.of(1, 2);
        String search = "abc";
        long now = dateService.getCurrentTimestamp();

        when(campaignVisibilityPort
                .findPreferredCampaignsWithVisibilityByUserAndManagementVisibility(
                        List.of("OU1"), USER_ID, now))
                .thenReturn(List.of(toCampaignVisibility("C1")));

        List<SurveyUnitToReview> content = List.of(
                mock(SurveyUnitToReview.class),
                mock(SurveyUnitToReview.class)
        );

        Page<SurveyUnitToReview> page = new PageImpl<>(content, pageable, 5);

        when(surveyUnitToReviewRepositoryPort.findSurveyUnitsToReview(
                anyList(), anyList(), eq(search), any(), eq(pageable)
        )).thenReturn(page);

        when(presenter.present(page)).thenReturn("RESULT");

        Object result = service.getSurveyUnitsToReview(
                USER_ID, null, search, null, pageable, presenter
        );

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


