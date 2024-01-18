package fr.insee.pearljam.api.domain;

import java.io.Serializable;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import fr.insee.pearljam.api.dto.phonenumber.PhoneNumberDto;

/**
 * Entity PhoneNumber : represent the entity table in DB
 * 
 * @author Corcaud Samuel
 * 
 */
@Entity
@Table
@Getter
@Setter
@NoArgsConstructor
public class PhoneNumber implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	private Source source;
	private boolean favorite;
	private String number;

	/**
	 * Person associated to the phone number
	 */
	@ManyToOne(fetch = FetchType.LAZY)
	private Person person;

	public PhoneNumber(PhoneNumberDto phoneNumber, Person person) {
		super();
		this.source = phoneNumber.getSource();
		this.favorite = phoneNumber.isFavorite();
		this.number = phoneNumber.getNumber();
		this.person = person;
	}

	/**
	 * Constructor with all fields
	 * 
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

}
