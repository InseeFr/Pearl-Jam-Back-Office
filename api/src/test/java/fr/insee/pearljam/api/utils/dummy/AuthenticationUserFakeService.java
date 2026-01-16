package fr.insee.pearljam.api.utils.dummy;

import fr.insee.pearljam.domain.security.model.AuthorityRole;
import fr.insee.pearljam.domain.security.port.userside.AuthenticatedUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;

@RequiredArgsConstructor
public class AuthenticationUserFakeService implements AuthenticatedUserService {
    private final Authentication authenticationUser;

    @Override
    public String getCurrentUserId() {
        return authenticationUser.getName();
    }

    @Override
    public boolean hasRole(AuthorityRole role) {
        return true;
    }

    @Override
    public boolean hasAnyRole(AuthorityRole... role) {
        return true;
    }
}
