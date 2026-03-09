package fr.insee.pearljam.infrastructure.persistence.organizationunit.entity;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

import fr.insee.pearljam.infrastructure.persistence.campaign.entity.CampaignDB;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Entity User : represent the entity table in DB
 * 
 * @author Corcaud Samuel
 * 
 */
@Entity
@Table(name = "user", schema = "public")
@NoArgsConstructor
@Getter
@Setter
public class UserDB implements Serializable {

	/**
	 * 
	 */
	@Serial
	private static final long serialVersionUID = -3490006766811003946L;

	/**
	 * The id of User
	 */
	@Id
	@Column(length = 50)
	private String id;

	/**
	 * The first name of the User
	 */
	@Column(length = 255)
	private String firstName;

	/**
	 * The last name of the User
	 */
	@Column(length = 255)
	private String lastName;

	/**
	 * The Organization Unit of the User
	 */
	@ManyToOne
	private OrganizationUnitDB organizationUnit;

	/**
	 * The List of campaign for the User
	 */
	@ManyToMany(cascade = CascadeType.ALL)
	@JoinTable(name = "preference", joinColumns = { @JoinColumn(name = "id_user") }, inverseJoinColumns = {
			@JoinColumn(name = "id_campaign") })
	private List<CampaignDB> campaigns;

	public UserDB(String id, String firstName, String lastName, OrganizationUnitDB organizationUnit) {
		super();
		this.id = id;
		this.firstName = firstName;
		this.lastName = lastName;
		this.organizationUnit = organizationUnit;
	}

}
