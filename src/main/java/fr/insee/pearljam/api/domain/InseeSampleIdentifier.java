package fr.insee.pearljam.api.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import fr.insee.pearljam.api.dto.sampleidentifier.SampleIdentifiersDto;

/**
 * Entity InseeSampleIdentifier : represent the entity table in DB
 * 
 * @author Claudel Benjamin
 * 
 */
@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class InseeSampleIdentifier extends SampleIdentifier {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1044333540440142996L;
	@Column
	private Integer bs;
	@Column(length = 1)
	private String ec;
	@Column
	private Integer le;
	@Column
	private Integer noi;
	@Column
	private Integer numfa;
	@Column
	private Integer rges;
	@Column
	private Integer ssech;
	@Column
	private Integer nolog;
	@Column
	private Integer nole;
	@Column(length = 50)
	private String autre;
	@Column(length = 50)
	private String nograp;

	public InseeSampleIdentifier(SampleIdentifiersDto sampleIdentifiers) {
		this.bs = sampleIdentifiers.getBs();
		this.ec = sampleIdentifiers.getEc();
		this.le = sampleIdentifiers.getLe();
		this.noi = sampleIdentifiers.getNoi();
		this.numfa = sampleIdentifiers.getNumfa();
		this.rges = sampleIdentifiers.getRges();
		this.ssech = sampleIdentifiers.getSsech();
		this.nolog = sampleIdentifiers.getNolog();
		this.nole = sampleIdentifiers.getNole();
		this.autre = sampleIdentifiers.getAutre();
		this.nograp = sampleIdentifiers.getNograp();
	}
}
