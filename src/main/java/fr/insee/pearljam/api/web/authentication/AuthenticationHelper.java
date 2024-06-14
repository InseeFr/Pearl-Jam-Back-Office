package fr.insee.pearljam.api.web.authentication;

import org.springframework.security.core.Authentication;

public interface AuthenticationHelper {
    /**
     * Retrieve the auth token of the current user
     *
     * @param auth authenticated user
     * @return auth token
     */
    String getAuthToken(Authentication auth);

    /**
     * Retrieve the user id from the current user
     *
     * @param authentication authenticated user
     * @return the user id
     */
    String getUserId(Authentication authentication);

    /**
     * Retrieve the authentication principal for current user
     *
     * @return {@link Authentication} the authentication user object
     */
    Authentication getAuthenticationPrincipal();
}