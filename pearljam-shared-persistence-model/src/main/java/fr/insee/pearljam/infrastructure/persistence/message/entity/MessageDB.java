package fr.insee.pearljam.infrastructure.persistence.message.entity;

import fr.insee.pearljam.infrastructure.persistence.campaign.entity.CampaignDB;
import fr.insee.pearljam.infrastructure.persistence.organizationunit.entity.OrganizationUnitDB;
import fr.insee.pearljam.infrastructure.persistence.organizationunit.entity.UserDB;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

/**
 * Entity Message : represent the entity table in DB
 *
 * @author Paul Guillemet
 */

@Entity
@Table(name = "message", schema = "public")
@NoArgsConstructor
@Getter
@Setter
public class MessageDB implements Serializable {

	@Serial
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
	private UserDB sender;

	/**
	 * The List of campaign for the Interviewer
	 */
	@ManyToMany
	@JoinTable(name = "ouMessageRecipient", joinColumns = {@JoinColumn(name = "message_id")}, inverseJoinColumns = {
			@JoinColumn(name = "organization_unit_id")})
	private List<OrganizationUnitDB> ouMessageRecipients;

	/**
	 * The List of campaign for the Interviewer
	 */
	@ManyToMany
	@JoinTable(name = "campaignMessageRecipient", joinColumns = {
			@JoinColumn(name = "message_id")}, inverseJoinColumns = {@JoinColumn(name = "campaign_id")})
	private List<CampaignDB> campaignMessageRecipients;

	/**
	 * The reference to visibility table
	 */
	@OneToMany(mappedBy = "message", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
	private List<MessageStatusDB> messageStatus;

	/**
	 * The phone number of the Message
	 */
	@Column
	public Long date;

	public MessageDB(String text, UserDB sender, Long date) {
		super();
		this.text = text;
		this.sender = sender;
		this.date = date;
	}

}
