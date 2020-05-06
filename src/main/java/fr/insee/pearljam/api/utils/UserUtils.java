package fr.insee.pearljam.api.utils;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.keycloak.adapters.springsecurity.token.KeycloakAuthenticationToken;
import org.keycloak.representations.IDToken;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class UserUtils {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(UserUtils.class);

	
	public static String handleUserInfoRequest(HttpServletRequest request, String attribute){
		String attributeValue = null;
		KeycloakAuthenticationToken principal = (KeycloakAuthenticationToken) request.getUserPrincipal();
		IDToken token = principal.getAccount().getKeycloakSecurityContext().getIdToken();
        Map<String, Object> otherClaims = token.getOtherClaims();
        
        if (otherClaims.containsKey(attribute)) {
        	attributeValue = String.valueOf(otherClaims.get(attribute));
        }
		LOGGER.info("Attribute " + attribute + " has value : " + attributeValue);
		return attributeValue;
	}	

}
