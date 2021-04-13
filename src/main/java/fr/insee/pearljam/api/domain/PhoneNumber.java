package fr.insee.pearljam.api.domain;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import fr.insee.pearljam.api.dto.phonenumber.PhoneNumberDto;

/**
* Entity PhoneNumber : represent the entity table in DB
* 
* @author Corcaud Samuel
* 
*/
@Entity
@Table
public class PhoneNumber implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	public Source source;
	
	/**
	 * Is the phone number is favorite
	 */
	public boolean favorite;
	
	/**
	 * phone numbers
	 */
	public String number;
	
	/**
	 * Person associated to the phone number
	 */
	@ManyToOne(fetch = FetchType.LAZY)
	public Person person;
	
	/**
	 * Default constructor
	 */
	public PhoneNumber() {
		super();
	}
	
	public PhoneNumber(PhoneNumberDto phoneNumber, Person person) {
		super();
		this.source = phoneNumber.getSource();
		this.favorite = phoneNumber.isFavorite();
		this.number = phoneNumber.getNumber();
		this.person = person;
	}
	
	/**
	 * Constructor with all fields
	 * @param id
	 * @param source
	 * @param favorite
	 * @param number
	 * @param person
	 */
	public PhoneNumber(Source source, boolean favorite, String number, Person person) {
		super();
		this.source = source;
		this.favorite = favorite;
		this.number = number;
		this.person = person;
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
	 * @return the source
	 */
	public Source getSource() {
		return source;
	}

	/**
	 * @return the favorite
	 */
	public boolean isFavorite() {
		return favorite;
	}

	/**
	 * @return the number
	 */
	public String getNumber() {
		return number;
	}

	/**
	 * @param source the source to set
	 */
	public void setSource(Source source) {
		this.source = source;
	}

	/**
	 * @param favorite the favorite to set
	 */
	public void setFavorite(boolean favorite) {
		this.favorite = favorite;
	}

	/**
	 * @param number the number to set
	 */
	public void setNumber(String number) {
		this.number = number;
	}
	/**
	 * @return the person
	 */
	public Person getPerson() {
		return person;
	}
	/**
	 * @param person the person to set
	 */
	public void setPerson(Person person) {
		this.person = person;
	}
}
