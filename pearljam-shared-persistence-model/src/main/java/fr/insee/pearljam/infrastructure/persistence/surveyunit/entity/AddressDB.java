package fr.insee.pearljam.infrastructure.persistence.surveyunit.entity;

import java.io.Serial;
import java.io.Serializable;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "address", schema = "public")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "dtype", discriminatorType = DiscriminatorType.STRING)
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public abstract class AddressDB implements Serializable {
	/**
	 * 
	 */
	@Serial
	private static final long serialVersionUID = 5680240598264620967L;

	/**
	 * The id of Address
	 */
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	protected Long id;

}
