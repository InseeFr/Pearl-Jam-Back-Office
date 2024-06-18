package fr.insee.pearljam.api.domain;

import java.io.Serializable;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Entity Message : represent the entity table in DB
 * 
 * @author Paul Guillemet
 * 
 */

@Entity
@Table
@NoArgsConstructor
@Getter
@Setter
public class Message implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1439604738865064692L;

	/**
	 * The id of Message
	 */
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	/**
	 * The first name of the Message
	 */
	@Column(length = 2000)
	private String text;

	/**
	 * The last name of the Message
	 */
	@ManyToOne
	private User sender;

	/**
	 * The List of campaign for the Interviewer
	 */
	@ManyToMany
	@JoinTable(name = "ouMessageRecipient", joinColumns = { @JoinColumn(name = "message_id") }, inverseJoinColumns = {
			@JoinColumn(name = "organization_unit_id") })
	private List<OrganizationUnit> ouMessageRecipients;

	/**
	 * The List of campaign for the Interviewer
	 */
	@ManyToMany
	@JoinTable(name = "campaignMessageRecipient", joinColumns = {
			@JoinColumn(name = "message_id") }, inverseJoinColumns = { @JoinColumn(name = "campaign_id") })
	private List<Campaign> campaignMessageRecipients;

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

	public Message(String text, User sender, Long date) {
		super();
		this.text = text;
		this.sender = sender;
		this.date = date;
	}

	public Message(String text, User sender, List<OrganizationUnit> ouMessageRecipients,
			List<Interviewer> interviewerMessageRecipients, Long date) {
		super();
		this.text = text;
		this.sender = sender;
		this.ouMessageRecipients = ouMessageRecipients;
		this.date = date;
	}

}
