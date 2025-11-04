package fr.insee.pearljam.surveyunit.infrastructure.rest.dto;

import fr.insee.pearljam.surveyunit.infrastructure.rest.dto.surveyunit.InterrogationOkNokResponseDto;
import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThat;

import fr.insee.pearljam.surveyunit.infrastructure.rest.dto.statedata.StateDataDto;

class InterrogationOkNokResponseDtoTest {

  @Test
  void testDefaultConstructorAndSettersAndGetters() {

    StateDataDto stateData = new StateDataDto();
    InterrogationOkNokResponseDto dto = new InterrogationOkNokResponseDto("testId", stateData);

    assertThat(dto.id()).isEqualTo("testId");
    assertThat(dto.stateData()).isEqualTo(stateData);
  }

  @Test
  void testConstructorWithIdOnly() {
    InterrogationOkNokResponseDto dto = new InterrogationOkNokResponseDto("testId");
    assertThat(dto.id()).isEqualTo("testId");
    assertThat(dto.stateData()).isNull();
  }

  @Test
  void testConstructorEmpty() {
    InterrogationOkNokResponseDto dto = new InterrogationOkNokResponseDto();
    assertThat(dto.id()).isNull();
    assertThat(dto.stateData()).isNull();
  }
}
