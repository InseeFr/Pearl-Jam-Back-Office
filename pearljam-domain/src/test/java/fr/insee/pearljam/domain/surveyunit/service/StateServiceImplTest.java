package fr.insee.pearljam.domain.surveyunit.service;

import fr.insee.pearljam.contracts.organizationunit.dto.OrganizationUnitDto;
import fr.insee.pearljam.contracts.surveyunit.dto.state.StateCountDto;
import fr.insee.pearljam.domain.campaign.port.out.CampaignRepository;
import fr.insee.pearljam.domain.campaign.port.out.VisibilityRepository;
import fr.insee.pearljam.domain.organizationunit.port.in.RelatedOrganizationUnitService;
import fr.insee.pearljam.domain.organizationunit.port.in.UserService;
import fr.insee.pearljam.domain.organizationunit.port.out.OrganizationUnitRepository;
import fr.insee.pearljam.domain.surveyunit.port.out.ClosingCauseRepository;
import fr.insee.pearljam.domain.surveyunit.port.out.CommunicationRequestRepository;
import fr.insee.pearljam.domain.surveyunit.port.out.InterviewerRepository;
import fr.insee.pearljam.domain.surveyunit.port.out.StateRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

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

  @InjectMocks
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
