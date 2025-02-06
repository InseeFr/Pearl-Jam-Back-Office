package fr.insee.pearljam.api.dto.surveyunit;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import fr.insee.pearljam.api.domain.*;
import fr.insee.pearljam.api.dto.contactoutcome.ContactOutcomeDto;
import fr.insee.pearljam.api.dto.interviewer.InterviewerDto;
import fr.insee.pearljam.api.surveyunit.dto.CommentDto;
import fr.insee.pearljam.domain.surveyunit.model.IdentificationState;
import lombok.Getter;
import lombok.Setter;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Getter
@Setter
public class SurveyUnitCampaignDto {
	private String id;
	private String displayName;
	private Integer ssech;
	private String location;
	private String city;
	private Long finalizationDate;
	private String campaign;
	private ClosingCauseType closingCause;
	private StateType state;
	private Boolean reading;
	private Boolean viewed;
	private String questionnaireState;
	private ContactOutcomeDto contactOutcome;
	private IdentificationState identificationState;
	private List<CommentDto> comments;

	@JsonIgnore // used to process identificationState server side, not exposed via API
	private IdentificationConfiguration identificationConfiguration;

	@JsonIgnoreProperties(value = { "surveyUnitCount" })
	private InterviewerDto interviewer;

	public SurveyUnitCampaignDto(SurveyUnit su) {
		super();

		this.id = su.getId();
		this.displayName = su.getDisplayName();
		this.reading = false;
		this.viewed = su.getViewed();
		if (su.getSampleIdentifier() instanceof InseeSampleIdentifier) {
			this.ssech = ((InseeSampleIdentifier) su.getSampleIdentifier()).getSsech();
		}
		if (su.getAddress() instanceof InseeAddress
				&& ((InseeAddress) su.getAddress()).getL6() != null
				&& ((InseeAddress) su.getAddress()).getL6().trim().contains(" ")) {
			String locationAndCity = ((InseeAddress) su.getAddress()).getL6();
			String[] splittedCityName = locationAndCity.split(" ");
			this.location = splittedCityName[0];
			this.city = Arrays.stream(splittedCityName).skip(1).collect(Collectors.joining(" "));
		}

		if (su.getInterviewer() != null) {
			this.interviewer = new InterviewerDto(su.getInterviewer());
		}
		this.finalizationDate = su.getStates().stream()
				.filter(s -> StateType.FIN.equals(s.getType()) || StateType.CLO.equals(s.getType()))
				.map(State::getDate)
				.max(Long::compareTo)
				.orElse(null);

		this.reading = su.getStates().stream()
				.anyMatch(s -> StateType.TBR.equals(s.getType()));
		State currentState = su.getStates().stream()
				.max(Comparator.comparing(State::getDate))
				.orElse(null);

		if (su.getClosingCause() != null && currentState != null && !currentState.getType().equals(StateType.CLO)) {
			this.closingCause = su.getClosingCause().getType();
		}
		this.state = currentState == null ? null : currentState.getType();
		this.campaign = su.getCampaign().getLabel();
		this.identificationConfiguration = su.getCampaign().getIdentificationConfiguration();
		this.interviewer = su.getInterviewer() != null ? new InterviewerDto(su.getInterviewer()) : null;
		this.comments = CommentDto.fromModel(su.getModelComments());
		if (su.getContactOucome() != null) {
			this.contactOutcome = new ContactOutcomeDto(su.getContactOucome());
		}
	}

}
