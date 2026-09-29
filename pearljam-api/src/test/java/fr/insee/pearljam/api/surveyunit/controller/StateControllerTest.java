package fr.insee.pearljam.api.surveyunit.controller;

import fr.insee.pearljam.contracts.surveyunit.dto.state.StateCountDto;
import fr.insee.pearljam.domain.organizationunit.port.in.RelatedOrganizationUnitService;
import fr.insee.pearljam.domain.security.port.in.AuthenticatedUserService;
import fr.insee.pearljam.domain.surveyunit.port.in.StateService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class StateControllerTest {

  @Mock
  private AuthenticatedUserService authenticatedUserService;

  @Mock
  private StateService stateService;

  @Mock
  private RelatedOrganizationUnitService relatedOrganizationUnitService;

  private StateController stateController;

  @BeforeEach
  void setup() {
    MockitoAnnotations.openMocks(this); // Initialise les mocks
    stateController = new StateController(stateService, relatedOrganizationUnitService, authenticatedUserService, true);
  }

  @Test
  @DisplayName("Test successful retrieval of interviewers' state count")
  void testGetInterviewersStateCount_Success() {
    // Given
    Long date = System.currentTimeMillis();
    String userId = "user123";

    StateCountDto stateCountDto = new StateCountDto("SIMPSONS2020X00", "Simpsons Campaign", Collections.emptyMap());
    List<StateCountDto> stateCountCampaignsDto = Collections.singletonList(stateCountDto);

    when(authenticatedUserService.getCurrentUserId()).thenReturn(userId);
    when(stateService.getStateCountByInterviewer(userId, date)).thenReturn(stateCountCampaignsDto);

    // When
    ResponseEntity<List<StateCountDto>> response = stateController.getInterviewersStateCount(date);

    // Then
    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertNotNull(response.getBody());
    assertEquals(1, response.getBody().size());
    assertEquals("SIMPSONS2020X00", response.getBody().getFirst().getIdDem());
    assertEquals("Simpsons Campaign", response.getBody().getFirst().getLabelDem());
    verify(authenticatedUserService).getCurrentUserId();
    verify(stateService).getStateCountByInterviewer(userId, date);
  }

  @Test
  @DisplayName("Test not found scenario for interviewers' state count")
  void testGetInterviewersStateCount_NotFound() {
    // Given
    Long date = 1672531200000L;
    String userId = "user123";

    when(authenticatedUserService.getCurrentUserId()).thenReturn(userId);
    when(stateService.getStateCountByInterviewer(userId, date)).thenReturn(null);

    // When
    ResponseEntity<List<StateCountDto>> response = stateController.getInterviewersStateCount(date);

    // Then
    assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    assertNull(response.getBody());
  }

  @Test
  @DisplayName("Test exception scenario when an error occurs while retrieving interviewers' state count")
  void testGetInterviewersStateCount_Exception() {
    // Given
    Long date = 1672531200000L;
    String userId = "user123";

    when(authenticatedUserService.getCurrentUserId()).thenReturn(userId);
    when(stateService.getStateCountByInterviewer(userId, date))
        .thenThrow(new RuntimeException("Unexpected error"));

    // When / Then
    assertThatThrownBy(() -> stateController.getInterviewersStateCount(date))
        .isInstanceOf(RuntimeException.class)
        .hasMessageContaining("Unexpected error");
  }
}
