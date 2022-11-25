package fr.insee.pearljam.api.service.impl;

import java.util.Arrays;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import fr.insee.pearljam.api.domain.Identification;
import fr.insee.pearljam.api.domain.IdentificationQuestions.CategoryQuestionValue;
import fr.insee.pearljam.api.domain.IdentificationQuestions.IdentificationQuestionValue;
import fr.insee.pearljam.api.domain.IdentificationQuestions.SituationQuestionValue;

import fr.insee.pearljam.api.repository.IdentificationRepository;
import fr.insee.pearljam.api.service.IdentificationService;

@Service
public class IdentificationServiceImpl implements IdentificationService {

    @Autowired
    IdentificationRepository identificationRepository;

    @Override
    public Identification findBySurveyUnitId(String id) {
        return identificationRepository.findBySurveyUnitId(id);
    }

    @Override
    public String getIdentificationState(Identification identification) {
        boolean identificationIsMissing = identification == null || identification.getIdentification() == null;
        if (identificationIsMissing)
            return "MISSING";

        boolean identificationIsFinished = Arrays
                .asList(IdentificationQuestionValue.DESTROY, IdentificationQuestionValue.UNIDENTIFIED)
                .contains(identification.getIdentification()) ||
                identification.getAccess() != null &&
                        (Arrays.asList(SituationQuestionValue.ABSORBED, SituationQuestionValue.NOORDINARY)
                                .contains(identification.getSituation())
                                ||
                                Arrays.asList(CategoryQuestionValue.SECONDARY, CategoryQuestionValue.VACANT)
                                        .contains(identification.getCategory())
                                ||
                                identification.getOccupant() != null);

        if (identificationIsFinished)
            return "FINISHED";
        return "ONGOING";
    }

}
