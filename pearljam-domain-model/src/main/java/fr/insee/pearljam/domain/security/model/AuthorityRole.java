package fr.insee.pearljam.domain.security.model;

/**
 * Application roles
 */
public enum AuthorityRole {
    ADMIN,
    WEBCLIENT,
    LOCAL_USER,
    NATIONAL_USER,
    INTERVIEWER;

    public static final String ROLE_PREFIX = "ROLE_";

    public String securityRole() {
        return ROLE_PREFIX + this.name();
    }
}
