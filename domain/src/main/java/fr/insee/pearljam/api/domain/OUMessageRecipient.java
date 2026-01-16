package fr.insee.pearljam.api.domain;

import java.io.Serial;
import java.io.Serializable;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
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
@NoArgsConstructor
@Getter
@Setter
public class OUMessageRecipient implements Serializable {

	@Serial
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

	public OUMessageRecipient(Message message, OrganizationUnit organizationUnit) {
		super();
		this.message = message;
		this.organizationUnit = organizationUnit;
	}

}
