package fr.insee.pearljam.surveyunit.infrastructure.rest.dto.surveyunit;

import com.fasterxml.jackson.annotation.JsonInclude;
import fr.insee.pearljam.surveyunit.infrastructure.rest.dto.statedata.StateDataDto;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record InterrogationOkNokResponseDto(
    String id,
    StateDataDto stateData
)
{
  public InterrogationOkNokResponseDto() {
    this(null, null);
  }

  public InterrogationOkNokResponseDto(String id) {
    this(id, null);
  }
}
