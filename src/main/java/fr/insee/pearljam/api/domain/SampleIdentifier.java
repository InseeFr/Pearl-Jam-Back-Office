package fr.insee.pearljam.api.domain;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
/**
* Entity SampleIdentifier : represent the entity table in DB
* 
* @author Claudel Benjamin
* 
*/
@Entity
public abstract class SampleIdentifier implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 5088394603526415418L;
	/**
	* The id of SampleIdentifier 
	*/
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
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
