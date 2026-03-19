package fr.insee.pearljam.domain.surveyunit.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.anyList;
import static org.mockito.Mockito.anyLong;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import fr.insee.pearljam.contracts.campaign.dto.CampaignProjection;
import fr.insee.pearljam.contracts.organizationunit.dto.OrganizationUnitDto;
import fr.insee.pearljam.contracts.surveyunit.dto.state.StateCountDto;
import fr.insee.pearljam.domain.campaign.port.out.CampaignRepository;
import fr.insee.pearljam.domain.campaign.port.out.VisibilityRepository;
import fr.insee.pearljam.domain.surveyunit.port.out.ClosingCauseRepository;
import fr.insee.pearljam.domain.surveyunit.port.out.InterviewerRepository;
import fr.insee.pearljam.domain.surveyunit.port.out.StateRepository;
import fr.insee.pearljam.domain.surveyunit.model.count.ClosingCauseCountProjection;
import fr.insee.pearljam.domain.surveyunit.model.count.CommunicationRequestCountProjection;
import fr.insee.pearljam.domain.surveyunit.model.count.StateCountProjection;
import fr.insee.pearljam.domain.surveyunit.port.out.CommunicationRequestRepository;
import fr.insee.pearljam.domain.organizationunit.port.in.UserService;
import fr.insee.pearljam.domain.organizationunit.port.in.RelatedOrganizationUnitService;
import fr.insee.pearljam.domain.organizationunit.port.out.OrganizationUnitRepository;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

class StateServiceImplTest {

  @Mock
  private UserService userService;
  @Mock
  private InterviewerRepository interviewerRepository;
  @Mock
  private CampaignRepository campaignRepository;
  @Mock
  private StateRepository stateRepository;
  @Mock
  private CommunicationRequestRepository communicationRequestRepository;
  @Mock
  private ClosingCauseRepository closingCauseRepository;
  @Mock
  private VisibilityRepository visibilityRepository;
  @Mock
  private OrganizationUnitRepository organizationRepository;
  @Mock
  private RelatedOrganizationUnitService relatedOrganizationUnitService;

  private StateServiceImpl stateService;

  private String userId;
  private Long date;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
    stateService = new StateServiceImpl(campaignRepository, stateRepository,
            closingCauseRepository, interviewerRepository,
            visibilityRepository, organizationRepository,
            userService, relatedOrganizationUnitService,
            communicationRequestRepository
            );

    userId = "user1";
    date = System.currentTimeMillis();
  }

  @Test
  @DisplayName("Should return empty result when no organization units are present")
  void testGetStateCountByCampaigns_EmptyOrganizationUnits() {
    // Given
    when(userService.getUserOUs(userId, true)).thenReturn(Collections.emptyList());

    // When
    List<StateCountDto> result = stateService.getStateCountByCampaigns(userId, date);

    // Then
    assertTrue(result.isEmpty(), "Expected empty result when no organization units are present");
    verify(userService).getUserOUs(userId, true);
    verifyNoInteractions(campaignRepository, stateRepository, communicationRequestRepository, closingCauseRepository);
  }

  @Test
  @DisplayName("Should return two campaigns when organization units are present")
  void testGetStateCountByCampaigns_WithOrganizationUnits() {
    // Given
    List<OrganizationUnitDto> organizationUnits = Arrays.asList(new OrganizationUnitDto("OU1", "Unit 1"), new OrganizationUnitDto("OU2", "Unit 2"));
    when(userService.getUserOUs(userId, true)).thenReturn(organizationUnits);
    when(campaignRepository.findAllManagedAndNotClosedCampaignIdsByOuIds(anyList(), anyLong())).thenReturn(Arrays.asList("campaign1", "campaign2"));
    when(campaignRepository.findAllDtoByOuIds(anyList())).thenReturn(Arrays.asList(new CampaignProjection("campaign1", null, null, null), new CampaignProjection("campaign2", null, null, null)));
    when(stateRepository.findGroupedByCampaign(anyList(), anyList(), anyLong())).thenReturn(Collections.emptyList());
    when(communicationRequestRepository.getCommRequestCountByCampaigns(anyList(), anyList(), anyLong()))
            .thenReturn(Collections.emptyList());
    when(closingCauseRepository.getStateClosedByClosingCauseCountByCampaigns(anyList(), anyList(), anyLong()))
            .thenReturn(Collections.emptyList());

    // When
    List<StateCountDto> result = stateService.getStateCountByCampaigns(userId, date);

    // Then
    assertEquals(2, result.size(), "Expected two campaigns to be returned.");
    verify(userService).getUserOUs(userId, true);
    verify(campaignRepository).findAllManagedAndNotClosedCampaignIdsByOuIds(Arrays.asList("OU1", "OU2"), date);
    verify(stateRepository).findGroupedByCampaign(anyList(), anyList(), eq(date));
    verify(communicationRequestRepository).getCommRequestCountByCampaigns(anyList(), anyList(), anyLong());
  }

  @Test
  @DisplayName("Should return one campaign when date is null")
  void testGetStateCountByCampaigns_WithNullDate() {
    // Given
    List<OrganizationUnitDto> organizationUnits = List.of(new OrganizationUnitDto("OU1", "Unit 1"));
    when(userService.getUserOUs(userId, true)).thenReturn(organizationUnits);
    when(campaignRepository.findAllManagedAndNotClosedCampaignIdsByOuIds(anyList(), anyLong())).thenReturn(List.of("campaign1"));
    when(campaignRepository.findAllDtoByOuIds(anyList())).thenReturn(List.of(new CampaignProjection()));
    when(stateRepository.findGroupedByCampaign(anyList(), anyList(), anyLong())).thenReturn(Collections.emptyList());
    when(communicationRequestRepository.getCommRequestCountByCampaigns(anyList(), anyList(), anyLong()))
            .thenReturn(Collections.emptyList());
    when(closingCauseRepository.getStateClosedByClosingCauseCountByCampaigns(anyList(), anyList(), anyLong()))
            .thenReturn(Collections.emptyList());

    // When
    List<StateCountDto> result = stateService.getStateCountByCampaigns(userId, null);

    // Then
    assertEquals(1, result.size(), "Expected one campaign to be returned when date is null.");
    verify(userService).getUserOUs(userId, true);
    verify(campaignRepository).findAllManagedAndNotClosedCampaignIdsByOuIds(eq(List.of("OU1")), anyLong());
  }

  @Test
  @DisplayName("Should merge projection, communication request and closing cause counts")
  void testGetStateCountByCampaigns_WithCounts() {
    List<OrganizationUnitDto> organizationUnits = List.of(new OrganizationUnitDto("OU1", "Unit 1"));
    when(userService.getUserOUs(userId, true)).thenReturn(organizationUnits);
    when(campaignRepository.findAllManagedAndNotClosedCampaignIdsByOuIds(anyList(), anyLong())).thenReturn(List.of("campaign1"));
    when(campaignRepository.findAllDtoByOuIds(anyList())).thenReturn(List.of(new CampaignProjection()));

    StateCountProjection projection = new StateCountProjection("campaign1", 1L, 2L, 3L, 4L, 5L, 6L, 7L, 8L, 9L, 10L, 11L, 12L, 13L, 14L, 15L, 136L);
    when(stateRepository.findGroupedByCampaign(anyList(), anyList(), anyLong()))
            .thenReturn(List.of(projection));

    when(communicationRequestRepository.getCommRequestCountByCampaigns(anyList(), anyList(), anyLong()))
            .thenReturn(List.of(new CommunicationRequestCountProjection("campaign1", 20L, 21L)));
    when(closingCauseRepository.getStateClosedByClosingCauseCountByCampaigns(anyList(), anyList(), anyLong()))
            .thenReturn(List.of(new ClosingCauseCountProjection("campaign1", 5L, 0L, 0L, 0L)));

    List<StateCountDto> result = stateService.getStateCountByCampaigns(userId, date);

    StateCountDto dto = result.getFirst();
    assertEquals(136L, dto.getTotal());
    assertEquals(20L, dto.getNoticeCount());
    assertEquals(21L, dto.getReminderCount());
    assertEquals(5L, dto.getNpaCount());
    assertEquals(1L, dto.getNvmCount());
    assertEquals(15L, dto.getNvaCount());
  }


  @Test
  @DisplayName("Should return an empty list when the user has no organizational units")
  void shouldReturnEmptyListWhenUserHasNoOrganizationUnits() {
    // Given
    when(userService.getUserOUs(userId, true)).thenReturn(Collections.emptyList());
    when(interviewerRepository.findIdsByOrganizationUnitsAndCampaignId(anyList(),anyList())).thenReturn(Collections.emptySet());

    // When
    List<StateCountDto> result = stateService.getStateCountByInterviewer(userId, date);

    // Then
    assertTrue(result.isEmpty(), "The list should be empty if the user has no organizational units.");
    verify(userService, times(2)).getUserOUs(userId, true);
  }

  @Test
  @DisplayName("Should return an empty list when the user has organizational units but no campaigns")
  void shouldReturnEmptyListWhenUserHasOrganizationUnitsButNoCampaigns() {
    // Given
    List<OrganizationUnitDto> organizationUnits = List.of(new OrganizationUnitDto("OU1", "Unit 1"));
    when(userService.getUserOUs(userId, true)).thenReturn(organizationUnits);
    when(campaignRepository.findAllCampaignIdsByOuIds(List.of("OU1"))).thenReturn(Collections.emptyList());

    // When
    List<StateCountDto> result = stateService.getStateCountByInterviewer(userId, date);

    // Then
    assertTrue(result.isEmpty(), "The list should be empty if the user has units but no campaigns.");
    verify(userService, times(2)).getUserOUs(userId, true);
    verify(campaignRepository).findAllCampaignIdsByOuIds(List.of("OU1"));
  }

}
