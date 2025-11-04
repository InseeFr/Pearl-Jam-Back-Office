package fr.insee.pearljam.security.domain.service;

import fr.insee.pearljam.security.domain.port.serverside.AuthenticatedUserHelperPort;
import fr.insee.pearljam.security.domain.port.userside.AuthenticatedUserService;
import fr.insee.pearljam.security.domain.model.AuthorityRole;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthenticationCurrentUserService implements AuthenticatedUserService {

    private final AuthenticatedUserHelperPort currentUserHelper;

    @Override
    public String getCurrentUserId() {
        return currentUserHelper.getCurrentUserId();
    }

    @Override
    public boolean hasRole(AuthorityRole role) {
        return currentUserHelper.hasRole(role);
    }

    @Override
    public boolean hasAnyRole(AuthorityRole... roles) {
        return currentUserHelper.hasAnyRole(roles);
    }
}
