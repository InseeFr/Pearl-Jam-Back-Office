package fr.insee.pearljam.domain.surveyunit.port.out.view;

import fr.insee.pearljam.domain.surveyunit.model.CommentType;
import fr.insee.pearljam.domain.surveyunit.model.StateType;
import fr.insee.pearljam.domain.surveyunit.model.contactoutcome.ContactOutcomeType;
import fr.insee.pearljam.domain.surveyunit.model.closingcause.ClosingCauseType;

public interface SurveyUnitCampaignView {
    String getId();
    String getDisplayName();
    Integer getSsech();
    String getAddressL6();
    StateType getCurrentStateType();
    ClosingCauseType getClosingCauseType();
    ContactOutcomeType getContactOutcomeType();
    String getInterviewerId();
    String getInterviewerFirstName();
    String getInterviewerLastName();
    Long getFinalizationDate();
    Boolean getReading();
    Boolean getViewed();
    CommentType getCommentType();
    String getCommentValue();
}
