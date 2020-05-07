package fr.insee.pearljam.api.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import fr.insee.pearljam.api.domain.ContactOutcome;
import fr.insee.pearljam.api.domain.ContactOutcomeType;
import fr.insee.pearljam.api.domain.SurveyUnit;
import fr.insee.pearljam.api.dto.contactoutcome.ContactOutcomeDto;

public interface ContactOutcomeRepository extends JpaRepository<ContactOutcome, Long> {

	ContactOutcomeDto findDtoBySurveyUnit(SurveyUnit surveyUnit);

	Optional<ContactOutcome> findBySurveyUnitAndType(SurveyUnit surveyUnit, ContactOutcomeType type);

}
