package fr.insee.pearljam.surveyunit.infrastructure.persistence.jpa.adapter;

import fr.insee.pearljam.surveyunit.infrastructure.persistence.jpa.entity.SurveyUnit;
import fr.insee.pearljam.surveyunit.infrastructure.persistence.jpa.repository.SurveyUnitRepository;
import fr.insee.pearljam.surveyunit.domain.model.Comment;
import fr.insee.pearljam.surveyunit.domain.port.serverside.CommentRepository;
import fr.insee.pearljam.surveyunit.domain.service.exception.SurveyUnitNotFoundException;
import fr.insee.pearljam.surveyunit.infrastructure.persistence.jpa.entity.CommentDB;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
public class CommentDaoAdapter implements CommentRepository {
    private final SurveyUnitRepository surveyUnitRepository;

    @Override
    @Transactional
    public void updateComment(Comment comment) throws SurveyUnitNotFoundException {
        SurveyUnit surveyUnit = surveyUnitRepository
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
