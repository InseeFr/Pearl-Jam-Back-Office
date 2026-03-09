package fr.insee.pearljam.api.surveyunit.dto.surveyunit;

import fr.insee.pearljam.infrastructure.persistence.surveyunit.entity.InseeSampleIdentifierDB;
import fr.insee.pearljam.infrastructure.persistence.surveyunit.entity.SampleIdentifierDB;
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

	public SampleIdentifiersDto(SampleIdentifierDB si) {
		super();
		if (si != null) {
			this.bs = ((InseeSampleIdentifierDB) si).getBs();
			this.ec = ((InseeSampleIdentifierDB) si).getEc();
			this.le = ((InseeSampleIdentifierDB) si).getLe();
			this.noi = ((InseeSampleIdentifierDB) si).getNoi();
			this.numfa = ((InseeSampleIdentifierDB) si).getNumfa();
			this.rges = ((InseeSampleIdentifierDB) si).getRges();
			this.ssech = ((InseeSampleIdentifierDB) si).getSsech();
			this.nolog = ((InseeSampleIdentifierDB) si).getNolog();
			this.nole = ((InseeSampleIdentifierDB) si).getNole();
			this.autre = ((InseeSampleIdentifierDB) si).getAutre();
			this.nograp = ((InseeSampleIdentifierDB) si).getNograp();
		}
	}

	@Override
	public String toString() {
		return "SampleIdentifiersDto [bs=" + bs + ", ec=" + ec + ", le=" + le + ", noi=" + noi + ", numfa=" + numfa
				+ ", rges=" + rges + ", ssech=" + ssech + ", nolog=" + nolog + ", nole=" + nole + ", autre=" + autre
				+ ", nograp=" + nograp + "]";
	}

}
