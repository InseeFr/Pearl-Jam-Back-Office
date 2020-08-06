package fr.insee.pearljam.api.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import fr.insee.pearljam.api.domain.State;
import fr.insee.pearljam.api.domain.SurveyUnit;
import fr.insee.pearljam.api.dto.state.StateDto;

/**
* StateRepository is the repository using to access to State table in DB
* 
* @author Claudel Benjamin
* 
*/
public interface StateRepository extends JpaRepository<State, Long> {
	
	/**
	 * This method retrieve the State in db the SurveyUnit associated
	 * @param SurveyUnit
	 * @return StateDto
	 */
	StateDto findFirstDtoBySurveyUnitOrderByDate(SurveyUnit surveyUnit);

	List<StateDto> findAllDtoBySurveyUnitId(String suId);

}
