package fr.insee.pearljam.contracts.organizationunit.dto;

import java.util.List;

public record OrganizationUnitTreeDto(
        OrganizationUnitDto root,
        List<OrganizationUnitDto> childOrganizationUnits
) {}
