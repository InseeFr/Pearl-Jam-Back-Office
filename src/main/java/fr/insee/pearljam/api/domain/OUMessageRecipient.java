package fr.insee.pearljam.api.domain;

import java.io.Serializable;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
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
public class OUMessageRecipient implements Serializable {

	private static final long serialVersionUID = 1L;

	@EmbeddedId
	private OUMessageRecipientId messageRecipientId;

	/**
	 * The id of MessageRecipient
	 */
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "message_id", insertable = false, updatable = false)
	private Message message;

	/**
	 * The last name of the MessageRecipient
	 */
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "organization_unit_id", insertable = false, updatable = false)
	private OrganizationUnit organizationUnit;

	public OUMessageRecipient() {
		super();
	}

	public OUMessageRecipient(Message message, OrganizationUnit organizationUnit) {
		super();
		this.message = message;
		this.organizationUnit = organizationUnit;
	}

	/**
	 * @return id of comment
	 */
	public OUMessageRecipientId getId() {

		return messageRecipientId;
	}

	public void setId(OUMessageRecipientId messageRecipientId) {
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
	 * @return the email of the MessageRecipient
	 */
	public OrganizationUnit getOrganizationUnit() {
		return organizationUnit;
	}

	/**
	 * @param the email of the MessageRecipient
	 */
	public void setOrganizationUnit(OrganizationUnit organizationUnit) {
		this.organizationUnit = organizationUnit;
	}
}
