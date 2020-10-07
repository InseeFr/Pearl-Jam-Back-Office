package fr.insee.pearljam.api.dto.campaign;

import com.fasterxml.jackson.annotation.JsonInclude;

import fr.insee.pearljam.api.domain.Campaign;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class CollectionDatesDto {
	private Long startDate;
	private Long endDate;

	public CollectionDatesDto(Long startDate, Long endDate) {
		super();
		this.startDate = startDate;
		this.endDate = endDate;
	}

	public CollectionDatesDto(Campaign campaign) {
		super();
		this.startDate = campaign.getStartDate();
		this.endDate = campaign.getEndDate();
	}


	public CollectionDatesDto() {
		super();
	}

	/**
	 * @return the collectionStartDate
	 */
	public Long getStartDate() {
		return startDate;
	}

	/**
	 * @param collectionStartDate the collectionStartDate to set
	 */
	public void setStartDate(Long startDate) {
		this.startDate = startDate;
	}

	/**
	 * @return the collectionEndDate
	 */
	public Long getEndDate() {
		return endDate;
	}

	/**
	 * @param collectionEndDate the collectionEndDate to set
	 */
	public void setEndDate(Long endDate) {
		this.endDate = endDate;
	}


}
