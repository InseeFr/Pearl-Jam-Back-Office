package fr.insee.pearljam.api.utils;

import static fr.insee.pearljam.api.configuration.auth.AuthConstants.*;
import fr.insee.pearljam.api.configuration.auth.AuthorityRoleEnum;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;

import java.time.Instant;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

public class AuthenticatedUserTestHelper {

        public final static Authentication AUTH_MANAGER = getAuthenticatedUser("abc",
                        AuthorityRoleEnum.LOCAL_USER);
        public final static Authentication AUTH_INTERVIEWER = getAuthenticatedUser("INTW1",
                        AuthorityRoleEnum.INTERVIEWER);
        public final static Authentication NOT_AUTHENTICATED = getNotAuthenticatedUser();

        public static Authentication getAuthenticatedUser(String id, AuthorityRoleEnum... roles) {
                List<? extends GrantedAuthority> authorities = Stream.of(roles)
                                .map(role -> new SimpleGrantedAuthority(ROLE_PREFIX + role.name())).toList();

                Map<String, Object> headers = Map.of("typ", "JWT");
                Map<String, Object> claims = Map.of(
                                "preferred_username", id,
                                "name", id,
                                "realmRoles", List.of("offline_access", "manager_local", "uma_authorization"));

                Jwt jwt = new Jwt("token-value", Instant.MIN, Instant.MAX, headers, claims);
                return new JwtAuthenticationToken(jwt, authorities, "Jean Dupont");
        }

        private static Authentication getNotAuthenticatedUser() {
                Map<String, String> principal = new HashMap<>();
                Authentication auth = new AnonymousAuthenticationToken("id", principal,
                                List.of(new SimpleGrantedAuthority(ROLE_PREFIX + ANONYMOUS)));
                auth.setAuthenticated(false);
                return auth;
        }
}
