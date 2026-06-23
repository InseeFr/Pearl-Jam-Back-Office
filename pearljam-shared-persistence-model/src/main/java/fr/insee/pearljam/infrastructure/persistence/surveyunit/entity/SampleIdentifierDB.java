package fr.insee.pearljam.infrastructure.persistence.surveyunit.entity;

import java.io.Serial;
import java.io.Serializable;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

/**
 * Entity SampleIdentifier : represent the entity table in DB
 * 
 * @author Claudel Benjamin
 * 
 */
@Entity
@Table(name = "sample_identifier", schema = "public")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "dtype", discriminatorType = DiscriminatorType.STRING)
@Getter
@Setter
public abstract class SampleIdentifierDB implements Serializable {
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
