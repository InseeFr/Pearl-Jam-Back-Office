package fr.insee.pearljam.domain.surveyunit.model.person;

import fr.insee.pearljam.api.domain.Title;

import java.util.Objects;
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

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (!(o instanceof Person p)) return false;
		return Objects.equals(id, p.id)
				&& title == p.title
				&& Objects.equals(firstName, p.firstName)
				&& Objects.equals(lastName, p.lastName)
				&& Objects.equals(email, p.email)
				&& Objects.equals(birthdate, p.birthdate)
				&& privileged == p.privileged
				&& isPanel == p.isPanel
				&& Objects.equals(phoneNumbers, p.phoneNumbers);
		// contactHistory intentionally excluded to break recursion
	}

	@Override
	public int hashCode() {
		return Objects.hash(id, title, firstName, lastName, email, birthdate,
				privileged, isPanel, phoneNumbers);
	}

}
