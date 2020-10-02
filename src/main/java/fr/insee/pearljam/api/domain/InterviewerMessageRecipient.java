package fr.insee.pearljam.api.domain;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.EmbeddedId;
import javax.persistence.JoinColumn;

import javax.persistence.FetchType;



import javax.persistence.ManyToOne;

/**
* Entity MessageRecipient : represent the entity table in DB
* 
* @author Paul Guillemet
* 
*/

@Entity
@Table
public class InterviewerMessageRecipient implements Serializable {
	
	private static final long serialVersionUID = 1L;

	@EmbeddedId
	private InterviewerMessageRecipientId messageRecipientId;


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
	
	
	public InterviewerMessageRecipient(){
    super();
	}

	public InterviewerMessageRecipient(Message message, Interviewer interviewer) {
		super();
		this.message = message;
		this.interviewer = interviewer;
	}
  
  	/**
	 * @return id of comment
	 */
	public InterviewerMessageRecipientId getId() {

		return messageRecipientId;
	}
  
	public void setId(InterviewerMessageRecipientId messageRecipientId) {
		this.messageRecipientId = messageRecipientId;
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
}
