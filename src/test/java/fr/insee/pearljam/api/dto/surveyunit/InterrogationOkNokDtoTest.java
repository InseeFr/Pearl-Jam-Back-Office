package fr.insee.pearljam.api.dto.surveyunit;

import org.junit.jupiter.api.Test;
import java.util.List;
import java.util.ArrayList;

import static org.assertj.core.api.Assertions.assertThat;

class InterrogationOkNokDtoTest {

  @Test
  void testDefaultConstructorAndSettersAndGetters() {

    List<InterrogationOkNokResponseDto> okList = new ArrayList<>();
    List<InterrogationOkNokResponseDto> nokList = new ArrayList<>();
    InterrogationOkNokDto dto = new InterrogationOkNokDto(okList, nokList);

    assertThat(dto.interrogationOK()).isEqualTo(okList);
    assertThat(dto.interrogationNOK()).isEqualTo(nokList);
  }

  @Test
  void testEmptyConstructorAndGetters() {

    InterrogationOkNokDto dto = new InterrogationOkNokDto();

    assertThat(dto.interrogationOK()).isNull();
    assertThat(dto.interrogationNOK()).isNull();
  }
}
