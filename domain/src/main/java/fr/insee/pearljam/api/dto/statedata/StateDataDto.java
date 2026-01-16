package fr.insee.pearljam.api.dto.statedata;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class StateDataDto {
	private String state;
	private Long date;
	private String currentPage;

}
