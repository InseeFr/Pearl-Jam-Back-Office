package fr.insee.pearljam.surveyunit.infrastructure.rest.dto;

import fr.insee.pearljam.surveyunit.infrastructure.persistence.jpa.entity.InseeSampleIdentifier;
import fr.insee.pearljam.surveyunit.infrastructure.persistence.jpa.entity.SampleIdentifier;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class SampleIdentifiersDto {
	private Integer bs;
	private String ec;
	private Integer le;
	private Integer noi;
	private Integer numfa;
	private Integer rges;
	private Integer ssech;
	private Integer nolog;
	private Integer nole;
	private String autre;
	private String nograp;

	public SampleIdentifiersDto(SampleIdentifier si) {
		super();
		if (si != null) {
			this.bs = ((InseeSampleIdentifier) si).getBs();
			this.ec = ((InseeSampleIdentifier) si).getEc();
			this.le = ((InseeSampleIdentifier) si).getLe();
			this.noi = ((InseeSampleIdentifier) si).getNoi();
			this.numfa = ((InseeSampleIdentifier) si).getNumfa();
			this.rges = ((InseeSampleIdentifier) si).getRges();
			this.ssech = ((InseeSampleIdentifier) si).getSsech();
			this.nolog = ((InseeSampleIdentifier) si).getNolog();
			this.nole = ((InseeSampleIdentifier) si).getNole();
			this.autre = ((InseeSampleIdentifier) si).getAutre();
			this.nograp = ((InseeSampleIdentifier) si).getNograp();
		}
	}

	@Override
	public String toString() {
		return "SampleIdentifiersDto [bs=" + bs + ", ec=" + ec + ", le=" + le + ", noi=" + noi + ", numfa=" + numfa
				+ ", rges=" + rges + ", ssech=" + ssech + ", nolog=" + nolog + ", nole=" + nole + ", autre=" + autre
				+ ", nograp=" + nograp + "]";
	}

}
