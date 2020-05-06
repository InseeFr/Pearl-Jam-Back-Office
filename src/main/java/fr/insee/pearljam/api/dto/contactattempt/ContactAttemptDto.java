package fr.insee.pearljam.api.dto.contactattempt;

import fr.insee.pearljam.api.domain.Status;

public class ContactAttemptDto {
	
	/**
	 * The date of the ContactAttemptDto
	 */
	Long date;
	
	/**
	 * The status of the ContactAttemptDto
	 */
    Status status;

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
	 * @return the status
	 */
	public Status getStatus() {
		return status;
	}

	/**
	 * @param status the status to set
	 */
	public void setStatus(Status status) {
		this.status = status;
	}
}
