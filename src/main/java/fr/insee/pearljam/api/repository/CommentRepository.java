package fr.insee.pearljam.api.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import fr.insee.pearljam.api.domain.Comment;
import fr.insee.pearljam.api.domain.SurveyUnit;
import fr.insee.pearljam.api.dto.comment.CommentDto;

public interface CommentRepository extends JpaRepository<Comment, Long> {
	List<CommentDto> findAllDtoBySurveyUnit(SurveyUnit surveyUnit);
}
