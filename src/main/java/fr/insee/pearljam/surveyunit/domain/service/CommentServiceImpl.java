package fr.insee.pearljam.surveyunit.domain.service;

import fr.insee.pearljam.surveyunit.domain.model.Comment;
import fr.insee.pearljam.surveyunit.domain.port.serverside.CommentRepository;
import fr.insee.pearljam.surveyunit.domain.port.userside.CommentService;

import fr.insee.pearljam.surveyunit.domain.service.exception.SurveyUnitNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * @author scorcaud
 *
 */
@Service
@Transactional
@Slf4j
@RequiredArgsConstructor
public class CommentServiceImpl implements CommentService {

    private final CommentRepository commentRepository;

    @Transactional
    public void updateSurveyUnitComment(Comment commentToUpdate) throws SurveyUnitNotFoundException {
        commentRepository.updateComment(commentToUpdate);
    }
}
