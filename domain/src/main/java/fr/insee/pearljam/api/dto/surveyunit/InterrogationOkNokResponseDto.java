package fr.insee.pearljam.api.dto.surveyunit;

import com.fasterxml.jackson.annotation.JsonInclude;
import fr.insee.pearljam.api.dto.statedata.StateDataDto;

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
