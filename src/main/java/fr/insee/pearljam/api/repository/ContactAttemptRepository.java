package fr.insee.pearljam.api.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

import fr.insee.pearljam.api.domain.ContactAttempt;
import fr.insee.pearljam.api.domain.Status;
import fr.insee.pearljam.api.domain.SurveyUnit;
import fr.insee.pearljam.api.dto.contactattempt.ContactAttemptDto;

public interface ContactAttemptRepository extends JpaRepository<ContactAttempt, Long> {

	List<ContactAttemptDto> findAllDtoBySurveyUnit(SurveyUnit surveyUnit);

	Optional<ContactAttempt> findBySurveyUnitAndStatus(SurveyUnit surveyUnit, Status status);

	@Transactional
	void deleteByIdAndStatus(Long id, Status status);


}
