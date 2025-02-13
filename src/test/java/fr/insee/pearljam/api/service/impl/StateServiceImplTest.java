package fr.insee.pearljam.api.service.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.anyList;
import static org.mockito.Mockito.anyLong;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import fr.insee.pearljam.api.dto.campaign.CampaignDto;
import fr.insee.pearljam.api.dto.organizationunit.OrganizationUnitDto;
import fr.insee.pearljam.api.dto.state.StateCountDto;
import fr.insee.pearljam.api.repository.CampaignRepository;
import fr.insee.pearljam.api.repository.ClosingCauseRepository;
import fr.insee.pearljam.api.repository.InterviewerRepository;
import fr.insee.pearljam.api.repository.StateRepository;
import fr.insee.pearljam.api.service.UserService;
import fr.insee.pearljam.domain.campaign.model.communication.CommunicationType;
import fr.insee.pearljam.domain.surveyunit.port.serverside.CommunicationRequestRepository;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
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

  @InjectMocks
  private StateServiceImpl stateService;

  private String userId;
  private Long date;
  private String campaignId;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
    userId = "user1";
    campaignId = "campaign1";
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
    when(campaignRepository.findAllCampaignIdsByOuIds(anyList())).thenReturn(Arrays.asList("campaign1", "campaign2"));
    when(stateRepository.getStateCountSumByCampaign(anyString(), anyList(), anyLong())).thenReturn(new HashMap<>());
    when(communicationRequestRepository.getCommunicationRequestCountByCampaignAndCommunicationTypeAndOrgaUnitId(anyString(), eq(CommunicationType.NOTICE), anyLong(), anyList())).thenReturn(0L);
    when(communicationRequestRepository.getCommunicationRequestCountByCampaignAndCommunicationTypeAndOrgaUnitId(anyString(), eq(CommunicationType.REMINDER), anyLong(), anyList())).thenReturn(0L);
    when(campaignRepository.findDtoById(anyString())).thenReturn(new CampaignDto());

    // When
    List<StateCountDto> result = stateService.getStateCountByCampaigns(userId, date);

    // Then
    assertEquals(2, result.size(), "Expected two campaigns to be returned.");
    verify(userService).getUserOUs(userId, true);
    verify(campaignRepository).findAllCampaignIdsByOuIds(Arrays.asList("OU1", "OU2"));
    verify(stateRepository, times(2)).getStateCountSumByCampaign(anyString(), anyList(), eq(date));
    verify(communicationRequestRepository, times(4)).getCommunicationRequestCountByCampaignAndCommunicationTypeAndOrgaUnitId(anyString(), any(), eq(date), anyList());
  }

  @Test
  @DisplayName("Should return one campaign when date is null")
  void testGetStateCountByCampaigns_WithNullDate() {
    // Given
    List<OrganizationUnitDto> organizationUnits = List.of(new OrganizationUnitDto("OU1", "Unit 1"));
    when(userService.getUserOUs(userId, true)).thenReturn(organizationUnits);
    when(campaignRepository.findAllCampaignIdsByOuIds(anyList())).thenReturn(List.of("campaign1"));
    when(stateRepository.getStateCountSumByCampaign(anyString(), anyList(), anyLong())).thenReturn(new HashMap<>());
    when(communicationRequestRepository.getCommunicationRequestCountByCampaignAndCommunicationTypeAndOrgaUnitId(anyString(), eq(CommunicationType.NOTICE), anyLong(), anyList())).thenReturn(0L);
    when(communicationRequestRepository.getCommunicationRequestCountByCampaignAndCommunicationTypeAndOrgaUnitId(anyString(), eq(CommunicationType.REMINDER), anyLong(), anyList())).thenReturn(0L);
    when(campaignRepository.findDtoById(anyString())).thenReturn(new CampaignDto());

    // When
    List<StateCountDto> result = stateService.getStateCountByCampaigns(userId, null);

    // Then
    assertEquals(1, result.size(), "Expected one campaign to be returned when date is null.");
    verify(userService).getUserOUs(userId, true);
    verify(campaignRepository).findAllCampaignIdsByOuIds(List.of("OU1"));
  }

  @Test
  @DisplayName("Should return an empty list when the user has no organizational units")
  void shouldReturnEmptyListWhenUserHasNoOrganizationUnits() {
    // Given
    when(userService.getUserOUs(userId, true)).thenReturn(Collections.emptyList());
    when(interviewerRepository.findIdsByOrganizationUnits(anyList())).thenReturn(Collections.emptySet()); // Mock de la méthode findIdsByOrganizationUnits

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

