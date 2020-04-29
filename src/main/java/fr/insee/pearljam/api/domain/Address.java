package fr.insee.pearljam.api.domain;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
/**
* Entity Address : represent the entity table in DB
* 
* @author Claudel Benjamin
* 
*/
@Entity
public abstract class Address {
	/**
	* The id of Address 
	*/
	@Id
	@GeneratedValue
	private Long id;
	
	/**
	* The GeographicalLocation associated to Address 
	*/
	@ManyToOne
	private GeographicalLocation geographicalLocation;

	/**
	 * @return the id
	 */
	public Long getId() {
		return id;
	}
	/**
	 * @param id the id to set
	 */
	public void setId(Long id) {
		this.id = id;
	}
	/**
	 * @return the geographicalLocation
	 */
	public GeographicalLocation getGeographicalLocation() {
		return geographicalLocation;
	}
	/**
	 * @param geographicalLocation the geographicalLocation to set
	 */
	public void setGeographicalLocation(GeographicalLocation geographicalLocation) {
		this.geographicalLocation = geographicalLocation;
	}
}
