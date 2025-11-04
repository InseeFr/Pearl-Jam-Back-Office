package fr.insee.pearljam.message.infrastructure.persistence.jpa.entity;

import java.io.Serial;
import java.io.Serializable;

import fr.insee.pearljam.message.domain.model.MessageStatusType;
import fr.insee.pearljam.surveyunit.infrastructure.persistence.jpa.entity.Interviewer;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
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
@Getter
@Setter
@NoArgsConstructor
public class MessageStatus implements Serializable {

	@Serial
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

	public MessageStatus(Message message, Interviewer interviewer, MessageStatusType status) {
		super();
		this.message = message;
		this.messageStatusId = new MessageStatusId(message.getId(), interviewer.getId());
		this.interviewer = interviewer;
		this.status = status;
	}

}
