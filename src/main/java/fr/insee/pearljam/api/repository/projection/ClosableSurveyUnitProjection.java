package fr.insee.pearljam.api.repository.projection;

import fr.insee.pearljam.domain.closingcause.model.ClosingCauseType;
import fr.insee.pearljam.domain.campaign.model.IdentificationConfiguration;
import fr.insee.pearljam.domain.surveyunit.model.IdentificationType;
import fr.insee.pearljam.domain.surveyunit.model.question.*;

public interface ClosableSurveyUnitProjection {

    String getId();

    String getDisplayName();

    Integer getSsech();

    String getAddressL6();

    String getCampaignLabel();

    ClosingCauseType getClosingCauseType();

    Long getFinalizationDate();

    IdentificationConfiguration getCampaignIdentificationConfiguration();

    IdentificationQuestionValue getIdentification();
    AccessQuestionValue getAccess();
    SituationQuestionValue getSituation();
    CategoryQuestionValue getCategory();
    OccupantQuestionValue getOccupant();
    IndividualStatusQuestionValue getIndividualStatus();
    InterviewerCanProcessQuestionValue getInterviewerCanProcess();
    NumberOfRespondentsQuestionValue getNumberOfRespondents();
    PresentInPreviousHomeQuestionValue getPresentInPreviousHome();
    HouseholdCompositionQuestionValue getHouseholdComposition();
    IdentificationType getIdentificationType();
    String getInterviewerFirstName();
    String getInterviewerLastName();
}
