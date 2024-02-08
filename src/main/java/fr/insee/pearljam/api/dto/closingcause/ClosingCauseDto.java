package fr.insee.pearljam.api.dto.closingcause;

import fr.insee.pearljam.api.domain.ClosingCauseType;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ClosingCauseDto {
	/**
	 * the date of ContactOutcome
	 */
	private Long date;
	/**
	 * the OutcomeType of ContactOutcome
	 */
	private ClosingCauseType type;

	public ClosingCauseDto(Long date, ClosingCauseType type) {
		super();
		this.date = date;
		this.type = type;
	}

	public ClosingCauseDto() {
		super();
	}

}
