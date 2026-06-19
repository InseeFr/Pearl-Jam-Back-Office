package fr.insee.pearljam.domain.surveyunit.service;

import fr.insee.pearljam.domain.campaign.port.in.DateService;
import fr.insee.pearljam.domain.campaign.port.out.CampaignVisibilityPort;
import fr.insee.pearljam.domain.campaign.readmodel.CampaignVisibility;
import fr.insee.pearljam.domain.campaign.service.dummy.FixedDateService;
import fr.insee.pearljam.domain.organizationunit.port.in.UserService;
import fr.insee.pearljam.domain.organizationunit.readmodel.OrganizationUnitSummary;
import fr.insee.pearljam.domain.surveyunit.port.out.SurveyUnitAssignedRepositoryPort;
import fr.insee.pearljam.domain.surveyunit.readmodel.SurveyUnitAssigned;
import fr.insee.pearljam.domain.surveyunit.service.exception.CampaignNotVisibleForUserException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@DisplayName("SurveyUnitAssignedService Tests")
class SurveyUnitAssignedServiceTest {

    private SurveyUnitAssignedService service;

    private SurveyUnitAssignedRepositoryPort repository;
    private UserService userService;
    private CampaignVisibilityPort campaignVisibilityPort;
    private DateService dateService = new FixedDateService();

    @BeforeEach
    void setUp() {
        repository = mock(SurveyUnitAssignedRepositoryPort.class);
        userService = mock(UserService.class);
        campaignVisibilityPort = mock(CampaignVisibilityPort.class);

        service = new SurveyUnitAssignedService(
            repository,
            userService,
            campaignVisibilityPort,
            dateService
        );
    }

    private void mockVisibleCampaigns(String... campaignIds) {
        when(campaignVisibilityPort
            .findPreferredCampaignsWithVisibilityByUserAndManagementVisibility(
                anyList(), anyString(), anyLong()
            ))
            .thenReturn(
                Arrays.stream(campaignIds)
                    .map(id -> new CampaignVisibility(
                        id,
                        "label",
                        "email@test.com",
                        1L,
                        2L,
                        3L,
                        4L,
                        5L,
                        6L
                    ))
                    .toList()
            );
    }

    @Test
    void should_return_all_visible_campaigns_when_no_campaignId() {
        // Given
        mockVisibleCampaigns("C1", "C2");

        when(userService.getUserOUsModel(anyString(), anyBoolean()))
            .thenReturn(List.of(new OrganizationUnitSummary("OU1", "label1")));
        Page<SurveyUnitAssigned> page = new PageImpl<>(List.of());

        when(repository.findSurveyUnitsAssigned(
            anyList(),
            any(),
            any()
        )).thenReturn(page);

        // When
        service.getSurveyUnitsAssigned("user1", null, null, Pageable.unpaged(), Page::toString);

        // Then
        verify(repository).findSurveyUnitsAssigned(
            eq(List.of("C1", "C2")),
            any(),
            any()
        );
    }

    @Test
    void should_use_only_campaignId_when_valid_and_visible() {
        // Given
        mockVisibleCampaigns("C1", "C2");

        when(userService.getUserOUsModel(anyString(), anyBoolean()))
            .thenReturn(List.of(new OrganizationUnitSummary("OU1", "label1")));

        Page<SurveyUnitAssigned> page = new PageImpl<>(List.of());

        when(repository.findSurveyUnitsAssigned(
            anyList(),
            any(),
            any()
        )).thenReturn(page);

        // When
        service.getSurveyUnitsAssigned("user1", "C1", null, Pageable.unpaged(), Page::toString);

        // Then
        verify(repository).findSurveyUnitsAssigned(
            eq(List.of("C1")),
            any(),
            any()
        );
    }

    @Test
    void should_throw_exception_when_campaign_not_visible() {
        // Given
        mockVisibleCampaigns("C1", "C2");

        when(userService.getUserOUsModel(anyString(), anyBoolean()))
            .thenReturn(List.of(new OrganizationUnitSummary("OU1", "label1")));

        Executable executable = () ->
            service.getSurveyUnitsAssigned(
                "user1",
                "C999",
                null,
                Pageable.unpaged(),
                Page::toString
            );

        assertThrows(CampaignNotVisibleForUserException.class, executable);
    }

    @Test
    void should_forward_search_and_pagination_to_repository() {
        // Given
        mockVisibleCampaigns("C1");

        when(userService.getUserOUsModel(anyString(), anyBoolean()))
            .thenReturn(List.of(new OrganizationUnitSummary("OU1", "label1")));

        Page<SurveyUnitAssigned> page = new PageImpl<>(List.of(
            new SurveyUnitAssigned(
            "SU1",
            "Display SU1",
            "SSECH1",
            "John",
            "Doe",
            "10000",
            "LILLE",
            "IN_PROGRESS",
            "NONE"
        )));
        when(repository.findSurveyUnitsAssigned(anyList(), eq("search"), any()))
            .thenReturn(page);

        // When
        service.getSurveyUnitsAssigned("user1", null, "search", Pageable.unpaged(), Page::toString);

        // Then
        verify(repository).findSurveyUnitsAssigned(
            eq(List.of("C1")),
            eq("search"),
            any()
        );
    }
}