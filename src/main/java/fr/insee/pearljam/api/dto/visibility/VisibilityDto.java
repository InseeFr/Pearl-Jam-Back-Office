package fr.insee.pearljam.api.dto.visibility;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class VisibilityDto {
	
	/**
	 * Collection start date of the visibility
	 */
	private Long startDate;
	
	/**
	 * Collection end date of the visibility
	 */
	private Long endDate;

	/**
	 * Default constructor for the entity
	 * @param startDate
	 * @param endDate
	 */
	public VisibilityDto(Long startDate, Long endDate) {
		super();
		this.startDate = startDate;
		this.endDate = endDate;
	}

	public Long getStartDate() {
		return startDate;
	}

	public void setStartDate(Long startDate) {
		this.startDate = startDate;
	}

	public Long getEndDate() {
		return endDate;
	}

	public void setEndDate(Long endDate) {
		this.endDate = endDate;
	}
}
