package fr.insee.pearljam.api.domain;

import java.io.Serializable;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

@Entity
public abstract class Address implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 5680240598264620967L;

	/**
	 * The id of Address
	 */
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	protected Long id;

	protected Address() {

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

}
