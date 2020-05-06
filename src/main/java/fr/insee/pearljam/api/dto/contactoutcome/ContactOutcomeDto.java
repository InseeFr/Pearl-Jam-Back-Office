package fr.insee.pearljam.api.dto.contactoutcome;

import fr.insee.pearljam.api.domain.ContactOutcomeType;

public class ContactOutcomeDto {
	
	/**
	 * The date of the ContactOutcomeDto
	 */
	 Long date;
	 
	 /**
	  * The type of the ContactOutcomeDto
	  */
     ContactOutcomeType type;
     
     /**
      * The total number of contact attempt for the current ContactOutcomeDto
      */
     Integer totalNumberOfContactAttempts;

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
	public ContactOutcomeType getType() {
		return type;
	}

	/**
	 * @param type the type to set
	 */
	public void setType(ContactOutcomeType type) {
		this.type = type;
	}

	/**
	 * @return the totalNumberOfContactAttempts
	 */
	public Integer getTotalNumberOfContactAttempts() {
		return totalNumberOfContactAttempts;
	}

	/**
	 * @param totalNumberOfContactAttempts the totalNumberOfContactAttempts to set
	 */
	public void setTotalNumberOfContactAttempts(Integer totalNumberOfContactAttempts) {
		this.totalNumberOfContactAttempts = totalNumberOfContactAttempts;
	}
}
