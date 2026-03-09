package fr.insee.pearljam.api.organizationunit.dto;

import fr.insee.pearljam.contracts.organizationunit.dto.OrganizationUnitDto;

import java.util.List;

public record OrganizationUnitTreeDto(
        OrganizationUnitDto root,
        List<OrganizationUnitDto> childOrganizationUnits
) {}
