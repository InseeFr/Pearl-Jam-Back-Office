package fr.insee.pearljam.api.domain;

import java.io.Serializable;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;
import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.JoinColumn;

import jakarta.persistence.FetchType;

import jakarta.persistence.ManyToOne;

/**
 * Entity MessageRecipient : represent the entity table in DB
 * 
 * @author Paul Guillemet
 * 
 */

@Entity
@Table
public class MessageStatus implements Serializable {

	private static final long serialVersionUID = 1L;

	@EmbeddedId
	private MessageStatusId messageStatusId;

	/**
	 * The id of MessageRecipient
	 */
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "message_id", insertable = false, updatable = false)
	private Message message;

	/**
	 * The first name of the MessageRecipient
	 */
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "interviewer_id", insertable = false, updatable = false)
	private Interviewer interviewer;

	@Column
	@Enumerated(EnumType.STRING)
	MessageStatusType status;

	public MessageStatus() {
		super();
	}

	public MessageStatus(Message message, Interviewer interviewer, MessageStatusType status) {
		super();
		this.message = message;
		this.messageStatusId = new MessageStatusId(message.getId(), interviewer.getId());
		this.interviewer = interviewer;
		this.status = status;
	}

	/**
	 * @return id of comment
	 */
	public MessageStatusId getId() {
		return messageStatusId;
	}

	public void setId(MessageStatusId messageStatusId) {
		this.messageStatusId = messageStatusId;
	}

	/**
	 * @return the text of the MessageRecipient
	 */
	public Message getMessage() {
		return message;
	}

	/**
	 * @param text of the MessageRecipient
	 */
	public void setMessage(Message message) {
		this.message = message;
	}

	/**
	 * @return the last name of the MessageRecipient
	 */
	public Interviewer getInterviewer() {
		return interviewer;
	}

	/**
	 * @param the last name of the MessageRecipient
	 */
	public void setInterviewer(Interviewer interviewer) {
		this.interviewer = interviewer;
	}

	/**
	 * @return the status
	 */
	public MessageStatusType getStatus() {
		return status;
	}

	/**
	 * @param status the status to set
	 */
	public void setStatus(MessageStatusType status) {
		this.status = status;
	}
}
