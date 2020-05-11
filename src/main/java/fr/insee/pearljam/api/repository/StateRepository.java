package fr.insee.pearljam.api.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import fr.insee.pearljam.api.domain.State;
import fr.insee.pearljam.api.domain.SurveyUnit;
import fr.insee.pearljam.api.dto.state.StateDto;

public interface StateRepository extends JpaRepository<State, Long> {
	StateDto findFirstBySurveyUnitOrderByDate(SurveyUnit surveyUnit);

}
