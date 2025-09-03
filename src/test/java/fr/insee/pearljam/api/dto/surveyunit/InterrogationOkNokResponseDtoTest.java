package fr.insee.pearljam.api.dto.surveyunit;

import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThat;

import fr.insee.pearljam.api.dto.statedata.StateDataDto;

class InterrogationOkNokResponseDtoTest {

  @Test
  void testDefaultConstructorAndSettersAndGetters() {
    InterrogationOkNokResponseDto dto = new InterrogationOkNokResponseDto();

    StateDataDto stateData = new StateDataDto();

    dto.setId("testId");
    dto.setStateData(stateData);

    assertThat(dto.getId()).isEqualTo("testId");
    assertThat(dto.getStateData()).isEqualTo(stateData);
  }

  @Test
  void testConstructorWithIdOnly() {
    InterrogationOkNokResponseDto dto = new InterrogationOkNokResponseDto("testId");
    assertThat(dto.getId()).isEqualTo("testId");
    assertThat(dto.getStateData()).isNull();
  }

  @Test
  void testConstructorWithIdAndStateData() {
    StateDataDto stateData = new StateDataDto();
    InterrogationOkNokResponseDto dto = new InterrogationOkNokResponseDto("testId", stateData);

    assertThat(dto.getId()).isEqualTo("testId");
    assertThat(dto.getStateData()).isEqualTo(stateData);
  }
}
