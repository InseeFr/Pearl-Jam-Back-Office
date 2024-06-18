package fr.insee.pearljam.api.utils;

import fr.insee.pearljam.domain.security.model.AuthorityRole;
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

        public final static Authentication AUTH_LOCAL_USER = getAuthenticatedUser("abc",
                        AuthorityRole.LOCAL_USER);
        public final static Authentication AUTH_INTERVIEWER = getAuthenticatedUser("INTW1",
                AuthorityRole.INTERVIEWER);
        public final static Authentication AUTH_ADMIN = getAuthenticatedUser("GUEST",
                AuthorityRole.ADMIN);
        public final static Authentication NOT_AUTHENTICATED = getNotAuthenticatedUser();

        public static Authentication getAuthenticatedUser(String id, AuthorityRole... roles) {
                List<? extends GrantedAuthority> authorities = Stream.of(roles)
                                .map(role -> new SimpleGrantedAuthority(role.securityRole())).toList();

                Map<String, Object> headers = Map.of("typ", "JWT");
                Map<String, Object> claims = Map.of(
                                "preferred_username", id,
                                "name", id,
                                "realmRoles", List.of("offline_access", "manager_local", "uma_authorization"));

                Jwt jwt = new Jwt("token-value", Instant.MIN, Instant.MAX, headers, claims);
                return new JwtAuthenticationToken(jwt, authorities, id);
        }

        private static Authentication getNotAuthenticatedUser() {
                Map<String, String> principal = new HashMap<>();
                Authentication auth = new AnonymousAuthenticationToken("id", principal,
                                List.of(new SimpleGrantedAuthority(AuthorityRole.ROLE_PREFIX + "ANONYMOUS")));
                auth.setAuthenticated(false);
                return auth;
        }
}
