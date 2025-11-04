package fr.insee.pearljam.surveyunit.infrastructure.persistence.jpa.entity;

import java.io.Serial;
import java.io.Serializable;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.Setter;

/**
 * Entity SampleIdentifier : represent the entity table in DB
 * 
 * @author Claudel Benjamin
 * 
 */
@Entity
@Getter
@Setter
public abstract class SampleIdentifier implements Serializable {
	/**
	 * 
	 */
	@Serial
	private static final long serialVersionUID = 5088394603526415418L;
	/**
	 * The id of SampleIdentifier
	 */
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

}
