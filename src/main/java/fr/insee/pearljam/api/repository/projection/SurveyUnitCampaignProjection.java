package fr.insee.pearljam.api.repository.projection;

import fr.insee.pearljam.api.domain.*;
import fr.insee.pearljam.domain.surveyunit.model.CommentType;

public interface SurveyUnitCampaignProjection {
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