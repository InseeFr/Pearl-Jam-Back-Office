package fr.insee.pearljam.surveyunit.infrastructure.rest.dto.surveyunit;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import fr.insee.pearljam.surveyunit.infrastructure.rest.dto.AddressDto;
import fr.insee.pearljam.surveyunit.infrastructure.rest.dto.closingcause.ClosingCauseDto;
import fr.insee.pearljam.surveyunit.infrastructure.rest.dto.contactattempt.ContactAttemptDto;
import fr.insee.pearljam.surveyunit.infrastructure.rest.dto.person.PersonDto;
import fr.insee.pearljam.surveyunit.infrastructure.rest.dto.SampleIdentifiersDto;
import fr.insee.pearljam.surveyunit.infrastructure.rest.dto.state.StateDto;
import fr.insee.pearljam.surveyunit.infrastructure.rest.dto.CommentDto;
import fr.insee.pearljam.surveyunit.infrastructure.rest.dto.ContactOutcomeDto;
import fr.insee.pearljam.surveyunit.infrastructure.rest.dto.identification.RawIdentificationDto;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class SurveyUnitContextDto {
	private String id;
	private String displayName;
	private List<PersonDto> persons;
	private AddressDto address;
	private String organizationUnitId;
	private Boolean priority;
	private String campaign;
	private SampleIdentifiersDto sampleIdentifiers;
	private List<StateDto> states;
	private List<ContactAttemptDto> contactAttempts;
	private ContactOutcomeDto contactOutcome;
	private ClosingCauseDto closingCause;
	private RawIdentificationDto identification;
	private Set<CommentDto> comments;

	/**
	 * This method checks if mandatory fields in the Survey Unit are valid or not
	 * 
	 * @return boolean
	 */
	@JsonIgnore
	public boolean isValid() {
		return (this.id != null && !this.id.isBlank()
				&& this.campaign != null && !this.campaign.isBlank()
				&& this.address != null
				&& this.persons != null && !this.persons.isEmpty()
				&& this.sampleIdentifiers != null
				&& this.priority != null);
	}
}
