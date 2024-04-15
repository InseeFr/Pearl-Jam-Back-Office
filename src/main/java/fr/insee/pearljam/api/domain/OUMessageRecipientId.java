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
public class OUMessageRecipientId implements Serializable {

	private static final long serialVersionUID = 1L;

	/**
	 * The organizationUnit Id
	 */
	@Column(name = "message_id")
	private Long messageId;

	/**
	 * The organizationUnit Id
	 */
	@Column(name = "organization_unit_id")
	private String organizationUnitId;

	/**
	 * Default constructor for the entity
	 */
	public OUMessageRecipientId() {
	}

	public OUMessageRecipientId(Long messageId, String organizationUnitId) {
		super();
		this.organizationUnitId = organizationUnitId;
		this.messageId = messageId;
	}

	/**
	 * @return the organizationUnitId
	 */
	public String getOrganizationUnitId() {
		return organizationUnitId;
	}

	/**
	 * @param organizationUnitId the organizationUnitId to set
	 */
	public void setOrganizationUnitId(String organizationUnitId) {
		this.organizationUnitId = organizationUnitId;
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
		return Objects.hash(getMessageId(), getOrganizationUnitId());
	}

}
