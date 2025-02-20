package fr.insee.pearljam.api.dto.closingcause;

import fr.insee.pearljam.api.domain.ClosingCauseType;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
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
