package fr.insee.pearljam.api.domain;

import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;

import javax.persistence.OneToMany;
import javax.persistence.ManyToOne;
import javax.persistence.CascadeType;
import javax.persistence.ManyToMany;
import javax.persistence.JoinTable;
import javax.persistence.JoinColumn;





/**
* Entity Message : represent the entity table in DB
* 
* @author Paul Guillemet
* 
*/

@Entity
@Table
public class Message {
	
	public Message(){
		super();
	}
	
	/**
	* The id of Message 
	*/
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	public Long id;

	/**
	* The first name of the Message 
	*/
	@Column(length=2000)
	public String text;
	
	/**
	* The last name of the Message 
	*/
	@ManyToOne
	public User sender;
	
	// /**
	// * The email of the Message 
	// */
	// @OneToMany(mappedBy = "message", cascade = CascadeType.ALL)
	// private List<OUMessageRecipient> ouMessageRecipients;
  
	// /**
	// * The email of the Message 
	// */
	// @OneToMany(mappedBy = "message", cascade = CascadeType.ALL)
	// private List<InterviewerMessageRecipient> InterviewerMessageRecipients;
  
  
  	/**
	 * The List of campaign for the Interviewer
	 */
	@ManyToMany
	@JoinTable(name = "interviewerMessageRecipient", joinColumns = { @JoinColumn(name = "message_id") }, inverseJoinColumns = { @JoinColumn(name = "interviewer_id") })
	public List<Interviewer> interviewerMessageRecipients;

	/**
	 * The List of campaign for the Interviewer
	 */
	@ManyToMany
	@JoinTable(name = "ouMessageRecipient", joinColumns = { @JoinColumn(name = "message_id") }, inverseJoinColumns = { @JoinColumn(name = "organization_unit_id") })
	public List<OrganizationUnit> ouMessageRecipients;
	
	/**
	 * The List of campaign for the Interviewer
	 */
	@ManyToMany
	@JoinTable(name = "campaignMessageRecipient", joinColumns = { @JoinColumn(name = "message_id") }, inverseJoinColumns = { @JoinColumn(name = "campaign_id") })
	public List<Campaign> campaignMessageRecipients;

	/**
	 * The reference to visibility table
	 */
	@OneToMany(mappedBy = "message", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
	private List<MessageStatus> messageStatus;

  

	/**
	 * The phone number of the Message
	 */
	@Column
	public Long date;
  
	public Message(String text, Long date) {
		super();
		this.text = text;
		this.date = date;
	}

  
	public Message(String text, User sender, Long date) {
		super();
		this.text = text;
		this.sender = sender;
		this.date = date;
	}


	public Message(String text, User sender, List<OrganizationUnit> ouMessageRecipients, List<Interviewer> interviewerMessageRecipients, Long date) {
		super();
		this.text = text;
	    this.sender = sender;
	    this.ouMessageRecipients = ouMessageRecipients;
	    this.interviewerMessageRecipients = interviewerMessageRecipients;
		this.date = date;
	}

	/**
	 * @return the id of the Message
	 */
	public Long getId() {
		return id;
	}

	/**
	 * @param id of the Message
	 */
	public void setId(Long id) {
		this.id = id;
	}

	/**
	 * @return the text of the Message
	 */
	public String getText() {
		return text;
	}

	/**
	 * @param text of the Message
	 */
	public void setText(String text) {
		this.text = text;
	}

	/**
	 * @return the last name of the Message
	 */
	public User getSender() {
		return sender;
	}

	/**
	 * @param the last name of the Message
	 */
	public void setSender(User sender) {
		this.sender = sender;
	}

	/**
	 * @return the email of the Message
	 */
	public List<Interviewer> getInterviewerMessageRecipients() {
		return interviewerMessageRecipients;
	}

	/**
	 * @param the email of the Message
	 */
	public void setInterviewerMessageRecipients(List<Interviewer> interviewerMessageRecipients ) {
		this.interviewerMessageRecipients = interviewerMessageRecipients;
  }
  
  	/**
	 * @return the email of the Message
	 */
	public List<OrganizationUnit> getOuMessageRecipients() {
		return ouMessageRecipients;
	}

	/**
	 * @param the email of the Message
	 */
	public void setOuMessageRecipients(List<OrganizationUnit> ouMessageRecipients ) {
		this.ouMessageRecipients = ouMessageRecipients;
	}
	
  	/**
	 * @return the email of the Message
	 */
	public List<Campaign> getCampaignMessageRecipients() {
		return campaignMessageRecipients;
	}

	/**
	 * @param the email of the Message
	 */
	public void setCampaignMessageRecipients(List<Campaign> campaignMessageRecipients ) {
		this.campaignMessageRecipients = campaignMessageRecipients;
	}
  
    /**
	 * @return the email of the Message
	 */
	public List<MessageStatus> getMessageStatus() {
		return messageStatus;
	}

	/**
	 * @param the email of the Message
	 */
	public void setMessageStatus(List<MessageStatus> messageStatus ) {
		this.messageStatus = messageStatus;
	}

	/**
	 * @return the phone number of the Message
	 */
	public Long getDate() {
		return date;
	}

	/**
	 * @param the phone number of the Message
	 */
	public void setDate(Long date) {
		this.date = date;
	}
	
}
