package fr.insee.pearljam.domain.surveyunit.model.person;

import fr.insee.pearljam.api.domain.Title;

import java.util.Set;

public record Person(
		Long id,
		Title title,
		String firstName,
		String lastName,
		String email,
		Long birthdate,
		boolean privileged,
		boolean isPanel,
		Set<PhoneNumber> phoneNumbers,
		ContactHistory contactHistory) {
}
