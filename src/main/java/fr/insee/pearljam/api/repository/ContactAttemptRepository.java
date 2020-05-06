package fr.insee.pearljam.api.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import fr.insee.pearljam.api.domain.ContactAttempt;
import fr.insee.pearljam.api.domain.SurveyUnit;
import fr.insee.pearljam.api.dto.contactattempt.ContactAttemptDto;

public interface ContactAttemptRepository extends JpaRepository<ContactAttempt, Long> {

	List<ContactAttemptDto> findAllDtoBySurveyUnit(SurveyUnit surveyUnit);

}
