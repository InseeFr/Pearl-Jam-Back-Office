package fr.insee.pearljam.contracts.surveyunit.dto.surveyunit;

import com.fasterxml.jackson.annotation.JsonInclude;
import fr.insee.pearljam.contracts.surveyunit.dto.SurveyUnitCampaignContactOutcomeDto;
import fr.insee.pearljam.contracts.surveyunit.dto.interviewer.InterviewerCampaignDto;
import fr.insee.pearljam.domain.surveyunit.model.CommentType;
import fr.insee.pearljam.domain.surveyunit.model.StateType;
import fr.insee.pearljam.domain.surveyunit.model.closingcause.ClosingCauseType;
import fr.insee.pearljam.domain.surveyunit.port.out.view.SurveyUnitCampaignView;
import fr.insee.pearljam.domain.surveyunit.readmodel.SurveyUnitMappers;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Getter
@Setter
@AllArgsConstructor
public class SurveyUnitCampaignDto {
    private String id;
    private String displayName;
    private Integer ssech;
    private String location;
    private String city;
    private Long finalizationDate;
    private ClosingCauseType closingCause;
    private SurveyUnitCampaignContactOutcomeDto contactOutcome;
    private StateType state;
    private Boolean reading;
    private Boolean viewed;
    private InterviewerCampaignDto interviewer;
    private List<CommentDto> comments;

    public static SurveyUnitCampaignDto from(SurveyUnitCampaignView projection) {
        return new SurveyUnitCampaignDto(
                projection.getId(),
                projection.getDisplayName(),
                projection.getSsech(),
                SurveyUnitMappers.computeLocation(projection.getAddressL6()),
                SurveyUnitMappers.computeCity(projection.getAddressL6()),
                projection.getFinalizationDate(),
                SurveyUnitMappers.computeClosingCause(projection.getClosingCauseType(), projection.getCurrentStateType()),
                projection.getContactOutcomeType() == null ? null : new SurveyUnitCampaignContactOutcomeDto(projection.getContactOutcomeType()),
                projection.getCurrentStateType(),
                projection.getReading(),
                projection.getViewed(),
                computeInterviewer(projection.getInterviewerId(),
                        projection.getInterviewerFirstName(),
                        projection.getInterviewerLastName()),
                projection.getCommentType() == null ?
                        null :
                        computeComments(projection.getCommentType(), projection.getCommentValue())
        );
    }

    private static List<CommentDto> computeComments(CommentType commentType, String commentValue) {
        return List.of(new CommentDto(commentType, commentValue));
    }

    private static InterviewerCampaignDto computeInterviewer(String interviewerId, String interviewerFirstName, String interviewerLastName) {
        if (interviewerId == null || interviewerId.isEmpty()) {
            return null;
        }
        return new InterviewerCampaignDto(interviewerId, interviewerFirstName, interviewerLastName);
    }
}
