package fr.insee.pearljam.api.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

import fr.insee.pearljam.api.domain.ContactAttempt;
import fr.insee.pearljam.api.domain.Status;
import fr.insee.pearljam.api.domain.SurveyUnit;
import fr.insee.pearljam.api.dto.contactattempt.ContactAttemptDto;

/**
* ContactAttemptRepository is the repository using to access to ContactAttempt table in DB
* 
* @author scorcaud
* 
*/
public interface ContactAttemptRepository extends JpaRepository<ContactAttempt, Long> {

	/**
	 * Retrieve all the ContactAttempt in db by the SurveyUnit associated
	 * @param surveyUnit
	 * @return ContactAttemptDto
	 */
	List<ContactAttemptDto> findAllDtoBySurveyUnit(SurveyUnit surveyUnit);

	/**
	 * Retrieve the ContactAttempt in db by the SurveyUnit and his status
	 * @param SurveyUnit
	 * @param Status
	 * @return ContactAttempt
	 */
	Optional<ContactAttempt> findBySurveyUnitAndStatus(SurveyUnit surveyUnit, Status status);

	/**
	 * Delete the ContactAttempt in db by his id and status
	 * @param id
	 * @param status
	 */
	@Transactional
	void deleteByIdAndStatus(Long id, Status status);


}
