package fr.insee.pearljam.api.surveyunit.dto.person;

import fr.insee.pearljam.domain.surveyunit.model.Source;
import fr.insee.pearljam.domain.surveyunit.model.contacthistory.PhoneNumber;

public record PhoneNumberDto(
		Source source,
		boolean favorite,
		String number
) {

	public static PhoneNumber toModel(PhoneNumberDto phoneNumber) {
		return new PhoneNumber(phoneNumber.source(), phoneNumber.favorite(), phoneNumber.number());
	}

	public static PhoneNumberDto fromModel(PhoneNumber phoneNumber) {
		return new PhoneNumberDto(phoneNumber.source(), phoneNumber.favorite(), phoneNumber.number());
	}


}
