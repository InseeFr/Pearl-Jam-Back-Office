package fr.insee.pearljam.api.domain;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
/**
* Entity Address : represent the entity table in DB
* 
* @author Claudel Benjamin
* 
*/
import javax.persistence.SequenceGenerator;
@Entity
@SequenceGenerator(
name = "seqid-gen", 
sequenceName = "ADDRESS_SEQ" ,
initialValue = 10, allocationSize = 1)
public abstract class Address {
	/**
	* The id of Address 
	*/
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seqid-gen")
	protected Long id;
	
	/**
	* The GeographicalLocation associated to Address 
	*/
	@ManyToOne
	protected GeographicalLocation geographicalLocation;

	public Address(){
		
	}
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
