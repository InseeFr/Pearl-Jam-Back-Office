package fr.insee.pearljam.domain.surveyunit.service;

import fr.insee.pearljam.domain.surveyunit.model.Comment;
import fr.insee.pearljam.domain.surveyunit.port.out.CommentRepository;
import fr.insee.pearljam.domain.surveyunit.port.in.CommentService;

import fr.insee.pearljam.domain.surveyunit.service.exception.SurveyUnitNotFoundException;
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
