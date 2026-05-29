package fr.insee.pearljam.infrastructure.persistence.message.entity;

import java.io.Serial;
import java.io.Serializable;

import fr.insee.pearljam.infrastructure.persistence.organizationunit.entity.OrganizationUnitDB;
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
@Table(name = "oumessage_recipient", schema = "public")
@NoArgsConstructor
@Getter
@Setter
public class OUMessageRecipientDB implements Serializable {

	@Serial
	private static final long serialVersionUID = 1L;

	@EmbeddedId
	private OUMessageRecipientDBId messageRecipientId;

	/**
	 * The id of MessageRecipient
	 */
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "message_id", insertable = false, updatable = false)
	private MessageDB message;

	/**
	 * The last name of the MessageRecipient
	 */
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "organization_unit_id", insertable = false, updatable = false)
	private OrganizationUnitDB organizationUnit;

	public OUMessageRecipientDB(MessageDB message, OrganizationUnitDB organizationUnit) {
		super();
		this.message = message;
		this.organizationUnit = organizationUnit;
	}

}
