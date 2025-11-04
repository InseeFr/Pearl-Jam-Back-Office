package fr.insee.pearljam.surveyunit.infrastructure.rest.dto.surveyunit;

import java.util.List;
import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record InterrogationOkNokDto(
    List<InterrogationOkNokResponseDto> interrogationOK,
    List<InterrogationOkNokResponseDto> interrogationNOK
) {

  public InterrogationOkNokDto() {
    this(null, null);
  }
}
