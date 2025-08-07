package fr.insee.pearljam.api.dto.message;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class VerifyNameResponseDto {
	private String id;
	private String type;
	private String label;

}
