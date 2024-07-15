package fr.insee.pearljam.infrastructure.surveyunit.adapter;

import fr.insee.pearljam.api.domain.SurveyUnit;
import fr.insee.pearljam.api.repository.SurveyUnitRepository;
import fr.insee.pearljam.domain.surveyunit.model.Comment;
import fr.insee.pearljam.domain.surveyunit.port.serverside.CommentRepository;
import fr.insee.pearljam.domain.exception.SurveyUnitNotFoundException;
import fr.insee.pearljam.infrastructure.surveyunit.entity.CommentDB;
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
                .orElseThrow(SurveyUnitNotFoundException::new);

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
