package fr.insee.pearljam.domain.surveyunit.model.contacthistory;

import fr.insee.pearljam.api.domain.Source;

public record PhoneNumber(
        Source source,
        boolean favorite,
        String number
) {
}
