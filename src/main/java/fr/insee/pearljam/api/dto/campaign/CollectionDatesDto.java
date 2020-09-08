package fr.insee.pearljam.api.dto.campaign;

import fr.insee.pearljam.api.domain.Campaign;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class CollectionDatesDto {
	private Long collectionStartDate;
	private Long collectionEndDate;

	public CollectionDatesDto(Long collectionStartDate, Long collectionEndDate) {
		super();
		this.collectionStartDate = collectionStartDate;
		this.collectionEndDate = collectionEndDate;
	}

	public CollectionDatesDto(Campaign campaign) {
		super();
		this.collectionStartDate = campaign.getCollectionStartDate();
		this.collectionEndDate = campaign.getCollectionEndDate();
	}


	public CollectionDatesDto() {
		super();
	}

	/**
	 * @return the collectionStartDate
	 */
	public Long getCollectionStartDate() {
		return collectionStartDate;
	}

	/**
	 * @param collectionStartDate the collectionStartDate to set
	 */
	public void setCollectionStartDate(Long collectionStartDate) {
		this.collectionStartDate = collectionStartDate;
	}

	/**
	 * @return the collectionEndDate
	 */
	public Long getCollectionEndDate() {
		return collectionEndDate;
	}

	/**
	 * @param collectionEndDate the collectionEndDate to set
	 */
	public void setCollectionEndDate(Long collectionEndDate) {
		this.collectionEndDate = collectionEndDate;
	}


}
