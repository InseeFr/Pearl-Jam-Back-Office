package fr.insee.pearljam.api.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import fr.insee.pearljam.api.domain.ClosingCause;
import fr.insee.pearljam.api.domain.SurveyUnit;



public interface ClosingCauseRepository extends JpaRepository<ClosingCause, Long> {
	
	Optional<ClosingCause> findBySurveyUnit(SurveyUnit surveyUnit);
	
	
}
