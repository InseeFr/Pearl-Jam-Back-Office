package fr.insee.pearljam.api.web.authentication;

public class AuthenticationTokenException extends RuntimeException {
    public AuthenticationTokenException(String message) {
        super(message);
    }
}
