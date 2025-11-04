package fr.insee.pearljam.organization.infrastructure.persistence.jpa.entity;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

import fr.insee.pearljam.campaign.infrastructure.persistence.jpa.entity.Campaign;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
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
public class User implements Serializable {

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
	private OrganizationUnit organizationUnit;

	/**
	 * The List of campaign for the User
	 */
	@ManyToMany(cascade = CascadeType.ALL)
	@JoinTable(name = "preference", joinColumns = { @JoinColumn(name = "id_user") }, inverseJoinColumns = {
			@JoinColumn(name = "id_campaign") })
	private List<Campaign> campaigns;

	public User(String id, String firstName, String lastName, OrganizationUnit organizationUnit) {
		super();
		this.id = id;
		this.firstName = firstName;
		this.lastName = lastName;
		this.organizationUnit = organizationUnit;
	}

}
