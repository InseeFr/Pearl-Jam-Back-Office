package fr.insee.pearljam.infrastructure.security.adapter;

import fr.insee.pearljam.domain.security.port.out.AuthenticatedUserHelperPort;
import fr.insee.pearljam.domain.security.model.AuthorityRole;
import fr.insee.pearljam.domain.security.service.exception.AuthenticationTokenException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Component;

import java.util.stream.Stream;

@Component
@RequiredArgsConstructor
public class AuthenticatedCurrentUserHelperAdapter implements AuthenticatedUserHelperPort {

    @Override
    public String getCurrentUserId() {
        return getAuthenticationPrincipal().getName();
    }

    @Override
    public boolean hasRole(AuthorityRole role) {
        return getAuthenticationPrincipal().getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .anyMatch(authority -> authority.contains(role.securityRole()));
    }

    @Override
    public boolean hasAnyRole(AuthorityRole... roles) {
        return Stream.of(roles)
                .anyMatch(this::hasRole);
    }

    @Override
    public String getToken() {
        Authentication authentication = getAuthenticationPrincipal();
        if (authentication instanceof JwtAuthenticationToken auth) {
            return auth.getToken().getTokenValue();
        }
        throw new AuthenticationTokenException("Cannot retrieve token for the contact");
    }

    private Authentication getAuthenticationPrincipal() {
        return SecurityContextHolder.getContext().getAuthentication();
    }
}
