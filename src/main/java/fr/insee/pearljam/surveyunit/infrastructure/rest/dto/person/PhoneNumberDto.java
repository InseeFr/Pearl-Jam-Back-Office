package fr.insee.pearljam.surveyunit.infrastructure.rest.dto.person;

import fr.insee.pearljam.surveyunit.infrastructure.persistence.jpa.entity.PhoneNumber;
import fr.insee.pearljam.surveyunit.domain.model.Source;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class PhoneNumberDto {

	private Source source;
	private boolean favorite;
	private String number;

	public PhoneNumberDto(PhoneNumber pn) {
		super();
		this.source = pn.getSource();
		this.favorite = pn.isFavorite();
		this.number = pn.getNumber();
	}

}
