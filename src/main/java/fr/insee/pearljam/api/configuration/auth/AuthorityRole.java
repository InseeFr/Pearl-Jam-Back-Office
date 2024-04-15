package fr.insee.pearljam.api.configuration.auth;

import static fr.insee.pearljam.api.configuration.auth.AuthorityRoleEnum.*;

import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.springframework.security.core.Authentication;

public class AuthorityRole {
    private AuthorityRole() {
        throw new IllegalArgumentException("Constant class");
    }

    private static String generateRoleExpression(AuthorityRoleEnum... roles) {
        return Stream.of(roles)
                .map(Enum::name)
                .map(s -> String.format("'%s'", s))
                .collect(Collectors.joining(", "));
    }

    private static final String ANY_ROLE = generateRoleExpression(AuthorityRoleEnum.values());
    private static final String ADMIN_PRIVILEGES = generateRoleExpression(ADMIN, WEBCLIENT);

    public static final String HAS_ROLE_INTERVIEWER = String.format("hasRole(%s)", INTERVIEWER);
    public static final String HAS_ANY_ROLE = String.format("hasAnyRole(%s)", ANY_ROLE);
    public static final String HAS_ADMIN_PRIVILEGES = String.format("hasAnyRole(%s)", ADMIN_PRIVILEGES);

    public static final boolean autorityContainsRole(Authentication auth, AuthorityRoleEnum role) {
        String targetAutority = AuthConstants.generateAuthority(role);
        return auth.getAuthorities().stream()
                .anyMatch(authority -> authority.getAuthority().equals(targetAutority));

    }
}