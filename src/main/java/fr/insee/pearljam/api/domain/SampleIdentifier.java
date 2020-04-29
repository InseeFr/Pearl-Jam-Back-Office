package fr.insee.pearljam.api.domain;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
/**
* Entity SampleIdentifier : represent the entity table in DB
* 
* @author Claudel Benjamin
* 
*/
@Entity
public abstract class SampleIdentifier {
	/**
	* The id of Address 
	*/
	@Id
	@GeneratedValue
	private Long id;

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
	
}
