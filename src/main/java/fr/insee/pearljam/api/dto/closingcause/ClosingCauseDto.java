package fr.insee.pearljam.api.dto.closingcause;

import fr.insee.pearljam.api.domain.ClosingCauseType;

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
	/**
	 * @return the date
	 */
	public Long getDate() {
		return date;
	}
	/**
	 * @param date the date to set
	 */
	public void setDate(Long date) {
		this.date = date;
	}
	/**
	 * @return the type
	 */
	public ClosingCauseType getType() {
		return type;
	}
	/**
	 * @param type the type to set
	 */
	public void setType(ClosingCauseType type) {
		this.type = type;
	}

}
