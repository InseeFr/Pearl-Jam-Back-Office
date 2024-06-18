package fr.insee.pearljam.infrastructure.security.adapter;

import fr.insee.pearljam.domain.security.port.serverside.AuthenticatedUserHelperPort;
import fr.insee.pearljam.domain.security.model.AuthorityRole;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.Arrays;

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
        return Arrays.stream(roles)
                .anyMatch(this::hasRole);
    }

    private Authentication getAuthenticationPrincipal() {
        return SecurityContextHolder.getContext().getAuthentication();
    }
}
