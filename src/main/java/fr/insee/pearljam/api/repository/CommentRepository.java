package fr.insee.pearljam.api.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import fr.insee.pearljam.api.domain.Comment;
import fr.insee.pearljam.api.domain.CommentType;
import fr.insee.pearljam.api.domain.SurveyUnit;
import fr.insee.pearljam.api.dto.comment.CommentDto;

/**
* CommentRepository is the repository using to access to  Comment table in DB
* 
* @author scorcaud
* 
*/
public interface CommentRepository extends JpaRepository<Comment, Long> {
	
	/**
	 * Retrieve all the Comment in db by the SurveyUnit associated
	 * @param surveyUnit
	 * @return CommentDto
	 */
	List<CommentDto> findAllDtoBySurveyUnit(SurveyUnit surveyUnit);

	/**
	 * Retrieve the Comment in db by the SurveyUnit and the type of Comment associated
	 * @param SurveyUnit
	 * @param CommentType
	 * @return Comment
	 */
	Optional<Comment> findBySurveyUnitAndType(SurveyUnit surveyUnit, CommentType interviewer);
}
