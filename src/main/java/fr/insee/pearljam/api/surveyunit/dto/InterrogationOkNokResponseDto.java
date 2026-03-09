package fr.insee.pearljam.api.surveyunit.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import fr.insee.pearljam.api.state.dto.StateDataDto;

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
