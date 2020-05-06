package fr.insee.pearljam.api.domain;

import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
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
	@Column(length=50)
	public String id;
	
	/**
	* The label of Campaign 
	*/
	@Column(length=255)
	public String label;
	
	/**
	 * The start date of the Campaign
	 */
	public Long collectionStartDate;
	
	/**
	 * The end date of the Campaign
	 */
	public Long collectionEndDate;
	
	/**
	 * The reference to visibility table
	 */
	@OneToMany(mappedBy = "campaign", cascade = CascadeType.ALL, orphanRemoval = true)
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
	public Long getCollectionStartDate() {
		return collectionStartDate;
	}

	/**
	 * @param start date of the Campaign
	 */
	public void setCollectionStartDate(Long collectionStartDate) {
		this.collectionStartDate = collectionStartDate;
	}

	/**
	 * @return the end date of the Campaign
	 */
	public Long getCollectionEndDate() {
		return collectionEndDate;
	}

	/**
	 * @param end date of the Campaign
	 */
	public void setCollectionEndDate(Long collectionEndDate) {
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
