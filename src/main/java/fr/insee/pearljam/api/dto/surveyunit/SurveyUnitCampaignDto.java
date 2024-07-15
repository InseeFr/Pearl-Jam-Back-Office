package fr.insee.pearljam.api.dto.surveyunit;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;

import fr.insee.pearljam.api.domain.ClosingCauseType;
import fr.insee.pearljam.api.domain.InseeAddress;
import fr.insee.pearljam.api.domain.InseeSampleIdentifier;
import fr.insee.pearljam.api.domain.State;
import fr.insee.pearljam.api.domain.StateType;
import fr.insee.pearljam.api.domain.SurveyUnit;
import fr.insee.pearljam.api.surveyunit.dto.CommentDto;
import fr.insee.pearljam.api.dto.contactoutcome.ContactOutcomeDto;
import fr.insee.pearljam.api.dto.interviewer.InterviewerDto;
import lombok.Getter;
import lombok.Setter;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Getter
@Setter
public class SurveyUnitCampaignDto {
	private String id;
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
	private String identificationState;
	private List<CommentDto> comments;

	@JsonIgnoreProperties(value = { "surveyUnitCount" })
	private InterviewerDto interviewer;

	public SurveyUnitCampaignDto() {
		super();
	}

	public SurveyUnitCampaignDto(String id, Integer ssech, String location, String city, Long finalizationDate,
			Boolean reading, Boolean viewed, InterviewerDto interviewer) {
		super();
		this.id = id;
		this.ssech = ssech;
		this.location = location;
		this.city = city;
		this.finalizationDate = finalizationDate;
		this.interviewer = interviewer;
		this.reading = reading;
		this.viewed = viewed;

	}

	public SurveyUnitCampaignDto(SurveyUnit su) {
		super();

		this.id = su.getId();
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
		this.interviewer = su.getInterviewer() != null ? new InterviewerDto(su.getInterviewer()) : null;
		this.comments = su.getDomainComments().stream()
				.map(CommentDto::fromModel)
				.toList();
		if (su.getContactOucome() != null) {
			this.contactOutcome = new ContactOutcomeDto(su.getContactOucome());
		}
	}

}
