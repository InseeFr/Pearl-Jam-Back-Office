package fr.insee.pearljam.contracts.surveyunit.dto.surveyunit;

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

	@Override
	public String toString() {
		return "SampleIdentifiersDto [bs=" + bs + ", ec=" + ec + ", le=" + le + ", noi=" + noi + ", numfa=" + numfa
				+ ", rges=" + rges + ", ssech=" + ssech + ", nolog=" + nolog + ", nole=" + nole + ", autre=" + autre
				+ ", nograp=" + nograp + "]";
	}

}
