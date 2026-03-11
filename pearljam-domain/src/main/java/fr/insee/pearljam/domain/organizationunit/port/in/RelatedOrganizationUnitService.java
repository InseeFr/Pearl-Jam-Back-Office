package fr.insee.pearljam.domain.organizationunit.port.in;

import java.util.List;

public interface RelatedOrganizationUnitService {

	/**
	 * This method retrieves the organizationUnit of the user as well as all of its children units as a list of String
	 * @param userId user id
	 * @return {@link List} of {@link String}
	 */
	List<String> getRelatedOrganizationUnits(String userId);
}
