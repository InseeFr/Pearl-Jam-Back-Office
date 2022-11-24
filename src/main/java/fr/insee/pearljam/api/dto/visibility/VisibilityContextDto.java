package fr.insee.pearljam.api.dto.visibility;

import com.fasterxml.jackson.annotation.JsonInclude;

import fr.insee.pearljam.api.domain.Visibility;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class VisibilityContextDto extends VisibilityDto {

	/**
	 * Organizational unit of the visibility
	 */
	private String organizationalUnit;

	public VisibilityContextDto() {
		super();
	}

	public VisibilityContextDto(Visibility visibility) {
		super();
		setManagementStartDate(visibility.getManagementStartDate());
		setInterviewerStartDate(visibility.getInterviewerStartDate());
		setIdentificationPhaseStartDate(visibility.getIdentificationPhaseStartDate());
		setCollectionStartDate(visibility.getCollectionStartDate());
		setCollectionEndDate(visibility.getCollectionEndDate());
		setEndDate(visibility.getEndDate());
		setOrganizationalUnit(visibility.getOrganizationUnit().getId());
	}

	public String getOrganizationalUnit() {
		return organizationalUnit;
	}

	public void setOrganizationalUnit(String organizationalUnit) {
		this.organizationalUnit = organizationalUnit;
	}

}
