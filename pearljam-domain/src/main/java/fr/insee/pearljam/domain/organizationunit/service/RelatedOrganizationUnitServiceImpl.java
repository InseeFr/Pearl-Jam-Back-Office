package fr.insee.pearljam.domain.organizationunit.service;

import fr.insee.pearljam.domain.organizationunit.port.in.RelatedOrganizationUnitService;
import fr.insee.pearljam.domain.organizationunit.port.out.OrganizationUnitRepository;
import fr.insee.pearljam.domain.organizationunit.port.out.UserRepository;
import fr.insee.pearljam.infrastructure.persistence.organizationunit.entity.UserDB;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class RelatedOrganizationUnitServiceImpl implements RelatedOrganizationUnitService {

    private final UserRepository userRepository;
    private final OrganizationUnitRepository organizationUnitRepository;

    @Override
    public List<String> getRelatedOrganizationUnits(String userId) {
        List<String> organizationUnitIds = new ArrayList<>();
        Optional<UserDB> user = userRepository.findByIdIgnoreCase(userId);

        if (user.isPresent()) {
            organizationUnitIds.add(user.get().getOrganizationUnit().getId());
            List<String> childrenIds = organizationUnitRepository.findChildrenId(user.get().getOrganizationUnit().getId());
            organizationUnitIds.addAll(childrenIds);
            for (String childId : childrenIds) {
                organizationUnitIds.addAll(organizationUnitRepository.findChildrenId(childId));
            }
        }

        return organizationUnitIds;
    }
}
