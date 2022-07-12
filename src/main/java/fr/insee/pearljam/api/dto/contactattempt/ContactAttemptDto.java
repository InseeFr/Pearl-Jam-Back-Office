package fr.insee.pearljam.api.dto.contactattempt;

import fr.insee.pearljam.api.domain.ContactAttempt;
import fr.insee.pearljam.api.domain.Status;

public class ContactAttemptDto {
	
	/**
	 * The date of the ContactAttemptDto
	 */
	private Long date;
	
	/**
	 * The status of the ContactAttemptDto
	 */
	private Status status;

	private String medium;
    
	public ContactAttemptDto(Long date, Status status, String medium) {
		super();
		this.date = date;
		this.status = status;
		this.medium = medium;
	}
	
	public ContactAttemptDto(ContactAttempt contactAttempt) {
		super();
		this.date = contactAttempt.getDate();
		this.status = contactAttempt.getStatus();
		this.medium = contactAttempt.getMedium();
	}
	

	public ContactAttemptDto() {
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


	public String getMedium() {
		return this.medium;
	}

	public void setMedium(String medium) {
		this.medium = medium;
	}


	@Override
	public String toString() {
		return "ContactAttemptDto [date=" + date + ", status=" + status + ", medium=" + medium + "]";
	}
	
}
