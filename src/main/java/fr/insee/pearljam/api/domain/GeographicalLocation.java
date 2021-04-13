package fr.insee.pearljam.api.domain;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
/**
* Entity GeographicalLocation : represent the entity table in DB
* 
* @author Claudel Benjamin
* 
*/
@Entity
@Table
public class GeographicalLocation implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -5032423587947124086L;

	/**
	* The id of GeographicalLocation 
	*/
	@Id
	@Column(length=50)
	private String id;
	
	/**
	* The label of GeographicalLocation 
	*/
	@Column(length=255)
	private String label;
	
	/**
	 * @return the id
	 */
	public String getId() {
		return id;
	}
	/**
	 * @param id the id to set
	 */
	public void setId(String id) {
		this.id = id;
	}
	/**
	 * @return the label
	 */
	public String getLabel() {
		return label;
	}
	/**
	 * @param label the label to set
	 */
	public void setLabel(String label) {
		this.label = label;
	}
}
