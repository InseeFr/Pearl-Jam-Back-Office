package fr.insee.pearljam.api.domain;

import java.io.Serializable;
import java.util.Objects;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

/**
 * This class is used to defines the association between OrganizationUnit and
 * Campaign tables.
 * 
 * @author Guillemet Paul
 */
@Embeddable
public class MessageStatusId implements Serializable {

	private static final long serialVersionUID = 1L;

	/**
	 * The organizationUnit Id
	 */
	@Column(name = "message_id")
	private Long messageId;

	/**
	 * The campaign Id
	 */
	@Column(name = "interviewer_id", nullable = true)
	private String interviewerId;

	/**
	 * Default constructor for the entity
	 */
	public MessageStatusId() {
	}

	public MessageStatusId(Long messageId, String interviewerId) {
		super();
		this.messageId = messageId;
		this.interviewerId = interviewerId;
	}

	/**
	 * @return the campaignId
	 */
	public Long getMessageId() {
		return messageId;
	}

	/**
	 * @param messageId the campaignId to set
	 */
	public void setCampaignId(Long messageId) {
		this.messageId = messageId;
	}

	@Override
	public boolean equals(Object o) {
		return (this == o);
	}

	@Override
	public int hashCode() {
		return Objects.hash(getMessageId(), getInterviewerId());
	}

	/**
	 * @return the campaignId
	 */
	public String getInterviewerId() {
		return interviewerId;
	}

	/**
	 * @param InterviewerId the campaignId to set
	 */
	public void setInterviewerId(String interviewerId) {
		this.interviewerId = interviewerId;
	}

}
