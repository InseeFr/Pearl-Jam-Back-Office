package fr.insee.pearljam.api.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import fr.insee.pearljam.api.domain.ContactOutcome;
import fr.insee.pearljam.api.domain.SurveyUnit;
import fr.insee.pearljam.api.dto.contactoutcome.ContactOutcomeDto;

/**
* ContactOutcomeRepository is the repository using to access to ContactOutcome table in DB
* 
* @author scorcaud
* 
*/
public interface ContactOutcomeRepository extends JpaRepository<ContactOutcome, Long> {

	/**
	 * Retrieve all the ContactOutcome in db by the SurveyUnit associated
	 * @param surveyUnit
	 * @return ContactOutcome
	 */
	ContactOutcomeDto findDtoBySurveyUnit(SurveyUnit surveyUnit);

	/**
	 * Retrieve the ContactOutcome in db by the SurveyUnit and the type of ContactOutcome
	 * @param SurveyUnit
	 * @return ContactOutcome
	 */
	Optional<ContactOutcome> findBySurveyUnit(SurveyUnit surveyUnit);

}
