package fr.insee.pearljam.api.domain;

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
public class GeographicalLocation {
	/**
	* The id of GeographicalLocation 
	*/
	@Id
	private String id;
	
	/**
	* The label of GeographicalLocation 
	*/
	@Column
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
