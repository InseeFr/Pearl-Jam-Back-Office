package fr.insee.pearljam.api.dto.surveyunit;

import com.fasterxml.jackson.annotation.JsonInclude;
import fr.insee.pearljam.api.domain.*;
import fr.insee.pearljam.api.dto.interviewer.InterviewerCampaignDto;
import fr.insee.pearljam.api.repository.projection.SurveyUnitCampaignProjection;
import fr.insee.pearljam.api.surveyunit.dto.CommentDto;
import fr.insee.pearljam.api.surveyunit.dto.SurveyUnitCampaignContactOutcomeDto;
import fr.insee.pearljam.domain.surveyunit.model.CommentType;
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

	public static SurveyUnitCampaignDto from(SurveyUnitCampaignProjection projection) {
		return new SurveyUnitCampaignDto(
				projection.getId(),
				projection.getDisplayName(),
				projection.getSsech(),
				SurveyUnitDtoMappers.computeLocation(projection.getAddressL6()),
				SurveyUnitDtoMappers.computeCity(projection.getAddressL6()),
				projection.getFinalizationDate(),
				SurveyUnitDtoMappers.computeClosingCause(projection.getClosingCauseType(), projection.getCurrentStateType()),
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
		if(interviewerId == null || interviewerId.isEmpty()) {
			return null;
		}
		return new InterviewerCampaignDto(interviewerId, interviewerFirstName, interviewerLastName);
	}
}
