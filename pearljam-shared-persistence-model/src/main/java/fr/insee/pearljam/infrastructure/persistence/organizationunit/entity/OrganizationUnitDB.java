package fr.insee.pearljam.infrastructure.persistence.organizationunit.entity;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

import fr.insee.pearljam.domain.organizationunit.model.OrganizationUnitType;
import fr.insee.pearljam.infrastructure.persistence.campaign.entity.VisibilityDB;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Entity OrganizationUnit : represent the entity table in DB
 * 
 * @author Claudel Benjamin
 * 
 */
@Entity
@Table(name = "organization_unit", schema = "public")
@Getter
@Setter
@NoArgsConstructor
public class OrganizationUnitDB implements Serializable {

	/**
	 * 
	 */
	@Serial
	private static final long serialVersionUID = 1L;

	/**
	 * Id of the OrganizationUnit
	 */
	@Id
	@Column(length = 50)
	private String id;

	/**
	 * The label OrganizationUnit
	 */
	@Column(length = 255)
	private String label;

	/**
	 * The OrganizationUnitType of the OrganizationUnit
	 */
	@Enumerated(EnumType.STRING)
	@Column(length = 8)
	private OrganizationUnitType type;

	@ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
	private OrganizationUnitDB organizationUnitParent;

	/**
	 * The visibilites of the OrganizationUnit
	 */
	@OneToMany(mappedBy = "organizationUnit", cascade = CascadeType.ALL, orphanRemoval = true)
	private List<VisibilityDB> visibilities;

	public OrganizationUnitDB(String id, String label, OrganizationUnitType type) {
		super();
		this.id = id;
		this.label = label;
		this.type = type;
	}

}
