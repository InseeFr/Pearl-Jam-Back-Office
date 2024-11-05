package fr.insee.pearljam.api.dto.surveyunit;

import java.util.List;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;

import fr.insee.pearljam.api.dto.address.AddressDto;
import fr.insee.pearljam.api.dto.closingcause.ClosingCauseDto;
import fr.insee.pearljam.api.surveyunit.dto.CommentDto;
import fr.insee.pearljam.api.dto.contactattempt.ContactAttemptDto;
import fr.insee.pearljam.api.dto.contactoutcome.ContactOutcomeDto;
import fr.insee.pearljam.api.surveyunit.dto.IdentificationDto;
import fr.insee.pearljam.api.dto.person.PersonDto;
import fr.insee.pearljam.api.dto.sampleidentifier.SampleIdentifiersDto;
import fr.insee.pearljam.api.dto.state.StateDto;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

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
	private IdentificationDto identification;
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
