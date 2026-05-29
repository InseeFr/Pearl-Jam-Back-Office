package fr.insee.pearljam.infrastructure.persistence.surveyunit.adapter;

import fr.insee.pearljam.infrastructure.persistence.surveyunit.entity.SurveyUnitDB;
import fr.insee.pearljam.infrastructure.persistence.surveyunit.jpa.SurveyUnitJpaRepository;
import fr.insee.pearljam.domain.surveyunit.model.Comment;
import fr.insee.pearljam.domain.surveyunit.port.out.CommentRepository;
import fr.insee.pearljam.domain.surveyunit.service.exception.SurveyUnitNotFoundException;
import fr.insee.pearljam.infrastructure.persistence.surveyunit.entity.CommentDB;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
public class CommentDaoAdapter implements CommentRepository {
    private final SurveyUnitJpaRepository surveyUnitRepository;

    @Override
    @Transactional
    public void updateComment(Comment comment) throws SurveyUnitNotFoundException {
        SurveyUnitDB surveyUnit = surveyUnitRepository
                .findById(comment.surveyUnitId())
                .orElseThrow(() -> new SurveyUnitNotFoundException(comment.surveyUnitId()));

        Set<CommentDB> existingComments = surveyUnit.getComments();

        CommentDB commentToUpdate = CommentDB.fromModel(surveyUnit, comment);

        Set<CommentDB> commentsToDelete = existingComments.stream()
                .filter(existingComment -> existingComment.getType().equals(commentToUpdate.getType()))
                .collect(Collectors.toSet());

        existingComments.removeAll(commentsToDelete);
        existingComments.add(commentToUpdate);

        surveyUnitRepository.save(surveyUnit);
    }
}
