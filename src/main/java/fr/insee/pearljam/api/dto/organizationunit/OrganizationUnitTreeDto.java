package fr.insee.pearljam.api.dto.organizationunit;

import java.util.List;

public record OrganizationUnitTreeDto(
        OrganizationUnitDto root,
        List<OrganizationUnitDto> childOrganizationUnits
) {}