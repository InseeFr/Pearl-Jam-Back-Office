package fr.insee.pearljam.domain.security.port.serverside;

import fr.insee.pearljam.domain.security.model.AuthorityRole;

/**
 * Helper used to retrieve current user information
 */
public interface AuthenticatedUserHelperPort {
    /**
     * Retrieve current user id
     * @return id of the current authenticated user
     */
    String getCurrentUserId();

    /**
     * Check if the current authenticated user has a specific role
     * @param role role to check
     * @return true if the current user has the specific role
     */
    boolean hasRole(AuthorityRole role);

    /**
     * Check if the current authenticated user has any of the specified roles
     * @param roles roles to check
     * @return true if the current user has one of the specified roles
     */
    boolean hasAnyRole(AuthorityRole... roles);
}
