package fr.insee.pearljam.infrastructure.persistence.message.entity;

import java.io.Serial;
import java.io.Serializable;

import fr.insee.pearljam.infrastructure.persistence.surveyunit.entity.InterviewerDB;
import fr.insee.pearljam.domain.message.model.MessageStatusType;
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
@Table(name = "message_status", schema = "public")
@Getter
@Setter
@NoArgsConstructor
public class MessageStatusDB implements Serializable {

	@Serial
	private static final long serialVersionUID = 1L;

	@EmbeddedId
	private MessageStatusDBId messageStatusId;

	/**
	 * The id of MessageRecipient
	 */
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "message_id", insertable = false, updatable = false)
	private MessageDB message;

	/**
	 * The first name of the MessageRecipient
	 */
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "interviewer_id", insertable = false, updatable = false)
	private InterviewerDB interviewer;

	@Column
	@Enumerated(EnumType.STRING)
	MessageStatusType status;

	public MessageStatusDB(MessageDB message, InterviewerDB interviewer, MessageStatusType status) {
		super();
		this.message = message;
		this.messageStatusId = new MessageStatusDBId(message.getId(), interviewer.getId());
		this.interviewer = interviewer;
		this.status = status;
	}

}
