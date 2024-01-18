package fr.insee.pearljam.api.domain;

import java.io.Serializable;
import java.util.Objects;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * This class is used to defines the association between OrganizationUnit and
 * Campaign tables.
 * 
 * @author Guillemet Paul
 */
@Embeddable
@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
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

	@Override
	public boolean equals(Object o) {
		return (this == o);
	}

	@Override
	public int hashCode() {
		return Objects.hash(getMessageId(), getOrganizationUnitId());
	}

}
