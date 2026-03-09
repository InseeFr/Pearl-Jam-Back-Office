package fr.insee.pearljam.api.organizationunit.dto;

import java.util.List;

public record OrganizationUnitTreeDto(
        OrganizationUnitDto root,
        List<OrganizationUnitDto> childOrganizationUnits
) {}
