package fr.insee.pearljam.api.domain;

import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;
import javax.persistence.Table;

/**
* Entity Campaign : represent the entity table in DB
* 
* @author Corcaud Samuel
* 
*/
@Entity
@Table
public class Campaign {
	
	/**
	* The id of Campaign 
	*/
	@Id
	public String id;
	
	/**
	* The label of Campaign 
	*/
	@Column(length=255)
	public String label;
	
	/**
	 * The start date of the Campaign
	 */
	public Date collectionStartDate;
	
	/**
	 * The end date of the Campaign
	 */
	public Date collectionEndDate;
	
	/**
	 * The reference to visibility table
	 */
	@OneToMany(mappedBy = "campaign", cascade = CascadeType.ALL)
    private List<Visibility> visibilities;
	
	/**
	 * @return the id of the Campaign
	 */
	public String getId() {
		return id;
	}

	/**
	 * @param id of the Campaign
	 */
	public void setId(String id) {
		this.id = id;
	}

	/**
	 * @return the label of the Campaign
	 */
	public String getLabel() {
		return label;
	}

	/**
	 * @param label of the Campaign
	 */
	public void setLabel(String label) {
		this.label = label;
	}

	/**
	 * @return the start date of the Campaign
	 */
	public Date getCollectionStartDate() {
		return collectionStartDate;
	}

	/**
	 * @param start date of the Campaign
	 */
	public void setCollectionStartDate(Date collectionStartDate) {
		this.collectionStartDate = collectionStartDate;
	}

	/**
	 * @return the end date of the Campaign
	 */
	public Date getCollectionEndDate() {
		return collectionEndDate;
	}

	/**
	 * @param end date of the Campaign
	 */
	public void setCollectionEndDate(Date collectionEndDate) {
		this.collectionEndDate = collectionEndDate;
	}

	/**
	 * @return the visibilities
	 */
	public List<Visibility> getVisibilities() {
		return visibilities;
	}

	/**
	 * @param visibilities the visibilities to set
	 */
	public void setVisibilities(List<Visibility> visibilities) {
		this.visibilities = visibilities;
	}
	
}
