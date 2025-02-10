package fr.insee.pearljam.api.controller;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import fr.insee.pearljam.api.dto.state.StateCountDto;
import fr.insee.pearljam.api.service.StateService;
import fr.insee.pearljam.domain.security.port.userside.AuthenticatedUserService;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

@ExtendWith(MockitoExtension.class)
class StateControllerTest {

  @Mock
  private AuthenticatedUserService authenticatedUserService;

  @Mock
  private StateService stateService;

  @InjectMocks
  private StateController stateController;

  @Test
  @DisplayName("Test successful retrieval of interviewers' state count by campaign")
  void testGetInterviewersStateCountByCampaign_Success() {
    // Given
    String campaignId = "SIMPSONS2020X00";
    Long date = System.currentTimeMillis();
    String userId = "user123";

    Map<String, Long> stateCountMap = new HashMap<>();
    stateCountMap.put("nvmCount", 5L);
    stateCountMap.put("nnsCount", 10L);
    stateCountMap.put("anvCount", 3L);

    StateCountDto stateCountDto = new StateCountDto(campaignId, "Simpsons Campaign", stateCountMap);

    when(authenticatedUserService.getCurrentUserId()).thenReturn(userId);
    when(stateService.getInterviewersStateCountByCampaign(userId, campaignId, date)).thenReturn(Collections.singletonList(stateCountDto));

    // When
    ResponseEntity<List<StateCountDto>> response = stateController.getInterviewersStateCountByCampaign(campaignId, date);

    // Then
    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertNotNull(response.getBody());
    assertEquals(1, response.getBody().size());

    StateCountDto returnedStateCountDto = response.getBody().getFirst();
    assertEquals("SIMPSONS2020X00", returnedStateCountDto.getIdDem());
    assertEquals("Simpsons Campaign", returnedStateCountDto.getLabelDem());
    assertEquals(5L, returnedStateCountDto.getNvmCount());
    assertEquals(10L, returnedStateCountDto.getNnsCount());
    assertEquals(3L, returnedStateCountDto.getAnvCount());

    verify(authenticatedUserService).getCurrentUserId();
    verify(stateService).getInterviewersStateCountByCampaign(userId, campaignId, date);
  }

  @Test
  @DisplayName("Test not found scenario for interviewers' state count by campaign")
  void testGetInterviewersStateCountByCampaign_NotFound() {
    // Given
    String campaignId = "testCampaign";
    String userId = "user123";
    Long date = 1672531200000L;

    when(authenticatedUserService.getCurrentUserId()).thenReturn(userId);
    when(stateService.getInterviewersStateCountByCampaign(userId, campaignId, date)).thenReturn(null);

    // When
    ResponseEntity<List<StateCountDto>> response = stateController.getInterviewersStateCountByCampaign(campaignId, date);

    // Then
    assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    assertNull(response.getBody());
  }

  @Test
  @DisplayName("Test exception scenario when an error occurs while retrieving interviewers' state count by campaign")
  void testGetInterviewersStateCountByCampaign_Exception() {
    // Given
    String campaignId = "testCampaign";
    String userId = "user123";
    Long date = 1672531200000L;

    when(authenticatedUserService.getCurrentUserId()).thenReturn(userId);
    when(stateService.getInterviewersStateCountByCampaign(userId, campaignId, date)).thenThrow(new RuntimeException("Unexpected error"));

    // When / Then
    assertThrows(RuntimeException.class, () -> stateController.getInterviewersStateCountByCampaign(campaignId, date));
  }

  @Test
  @DisplayName("Test successful retrieval of survey units' state count by campaign")
  void testGetCampaignsStateCount_Success() {
    // Given
    String userId = "user123";
    Long date = System.currentTimeMillis();

    StateCountDto stateCountDto = new StateCountDto("SIMPSONS2020X00", "Simpsons Campaign", Collections.emptyMap());
    List<StateCountDto> stateCountCampaignsDto = Collections.singletonList(stateCountDto);

    when(authenticatedUserService.getCurrentUserId()).thenReturn(userId);
    when(stateService.getStateCountByCampaigns(userId, date)).thenReturn(stateCountCampaignsDto);

    // When
    ResponseEntity<List<StateCountDto>> response = stateController.getCampaignsStateCount(date);

    // Then
    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertNotNull(response.getBody());
    assertEquals(1, response.getBody().size());
    assertEquals("SIMPSONS2020X00", response.getBody().getFirst().getIdDem());
    assertEquals("Simpsons Campaign", response.getBody().getFirst().getLabelDem());
    verify(authenticatedUserService).getCurrentUserId();
    verify(stateService).getStateCountByCampaigns(userId, date);
  }

  @Test
  @DisplayName("Test not found scenario for survey units' state count by campaign")
  void testGetCampaignsStateCount_NotFound() {
    // Given
    String userId = "user123";
    Long date = 1672531200000L;

    when(authenticatedUserService.getCurrentUserId()).thenReturn(userId);
    when(stateService.getStateCountByCampaigns(userId, date)).thenReturn(null);

    // When
    ResponseEntity<List<StateCountDto>> response = stateController.getCampaignsStateCount(date);

    // Then
    assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    assertNull(response.getBody());
  }

  @Test
  @DisplayName("Test exception scenario when an error occurs while retrieving survey units' state count by campaign")
  void testGetCampaignsStateCount_Exception() {
    // Given
    String userId = "user123";
    Long date = 1672531200000L;

    when(authenticatedUserService.getCurrentUserId()).thenReturn(userId);
    when(stateService.getStateCountByCampaigns(userId, date)).thenThrow(new RuntimeException("Unexpected error"));

    // When / Then
    assertThrows(RuntimeException.class, () -> stateController.getCampaignsStateCount(date));
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
    when(stateService.getStateCountByInterviewer(userId, date)).thenThrow(new RuntimeException("Unexpected error"));

    // When / Then
    assertThrows(RuntimeException.class, () -> stateController.getInterviewersStateCount(date));
  }
}
