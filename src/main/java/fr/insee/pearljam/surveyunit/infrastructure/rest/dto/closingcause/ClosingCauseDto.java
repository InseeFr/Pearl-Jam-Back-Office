package fr.insee.pearljam.surveyunit.infrastructure.rest.dto.closingcause;

import fr.insee.pearljam.surveyunit.infrastructure.persistence.jpa.entity.ClosingCauseType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ClosingCauseDto {
	/**
	 * the date of ContactOutcome
	 */
	private Long date;
	/**
	 * the OutcomeType of ContactOutcome
	 */
	private ClosingCauseType type;

}
