package fr.insee.pearljam.api.dto.surveyunit;


import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class InterrogationOkNokDto {

  List<InterrogationOkNokResponseDto> interrogationOK;
  List<InterrogationOkNokResponseDto> interrogationNOK;
	
	public InterrogationOkNokDto(List<InterrogationOkNokResponseDto> interrogationOK, List<InterrogationOkNokResponseDto> interrogationNOK) {
		super();
		this.interrogationOK = interrogationOK;
		this.interrogationNOK = interrogationNOK;
	}

	public InterrogationOkNokDto() {
		super();
	}


}
