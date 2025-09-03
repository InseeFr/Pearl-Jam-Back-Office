package fr.insee.pearljam.api.dto.surveyunit;

import org.junit.jupiter.api.Test;
import java.util.List;
import java.util.ArrayList;

import static org.assertj.core.api.Assertions.assertThat;

class InterrogationOkNokDtoTest {

  @Test
  void testDefaultConstructorAndSettersAndGetters() {
    InterrogationOkNokDto dto = new InterrogationOkNokDto();

    List<InterrogationOkNokResponseDto> okList = new ArrayList<>();
    List<InterrogationOkNokResponseDto> nokList = new ArrayList<>();

    dto.setInterrogationOK(okList);
    dto.setInterrogationNOK(nokList);

    assertThat(dto.getInterrogationOK()).isEqualTo(okList);
    assertThat(dto.getInterrogationNOK()).isEqualTo(nokList);
  }

  @Test
  void testAllArgsConstructor() {
    List<InterrogationOkNokResponseDto> okList = new ArrayList<>();
    List<InterrogationOkNokResponseDto> nokList = new ArrayList<>();

    InterrogationOkNokDto dto = new InterrogationOkNokDto(okList, nokList);

    assertThat(dto.getInterrogationOK()).isEqualTo(okList);
    assertThat(dto.getInterrogationNOK()).isEqualTo(nokList);
  }
}
