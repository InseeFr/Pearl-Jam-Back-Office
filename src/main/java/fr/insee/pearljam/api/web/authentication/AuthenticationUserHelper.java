package fr.insee.pearljam.api.web.authentication;

import fr.insee.pearljam.api.configuration.auth.AuthConstants;
import fr.insee.pearljam.api.configuration.properties.ApplicationProperties;
import lombok.AllArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.core.AbstractOAuth2Token;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class AuthenticationUserHelper implements AuthenticationHelper {
    private final ApplicationProperties applicationProperties;

    @Override
    public String getAuthToken(Authentication auth) {
        if (auth == null) {
            throw new AuthenticationTokenException("Cannot retrieve token for the user.");
        }
        AbstractOAuth2Token token = (AbstractOAuth2Token) auth.getCredentials();
        return token.getTokenValue();
    }

    @Override
    public String getUserId(Authentication authentication) {
        switch (applicationProperties.auth()) {
            case NOAUTH -> {
                return AuthConstants.GUEST;
            }
            case KEYCLOAK -> {
                if (authentication.getCredentials() instanceof Jwt jwt) {
                    return jwt.getClaims().get(AuthConstants.PREFERRED_USERNAME).toString();
                }
                throw new AuthenticationTokenException("Cannot retrieve token for the user.");
            }
            default -> throw new AuthenticationTokenException("No authentication mode used");
        }
    }

    @Override
    public Authentication getAuthenticationPrincipal() {
        return SecurityContextHolder.getContext().getAuthentication();
    }
}
