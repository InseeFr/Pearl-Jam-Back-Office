package fr.insee.pearljam.api.configuration.auth;

public class AuthConstants {
    private AuthConstants() {
        throw new IllegalStateException("Constants class");
    }

    public static final String ROLE_PREFIX = "ROLE_";
    public static final String GUEST = "GUEST";
    public static final String ANONYMOUS = "ANONYMOUS";
    public static final String PREFERRED_USERNAME = "preferred_username";

    public static final String generateAuthority(AuthorityRoleEnum are) {
        return AuthConstants.ROLE_PREFIX + are;
    }
}
