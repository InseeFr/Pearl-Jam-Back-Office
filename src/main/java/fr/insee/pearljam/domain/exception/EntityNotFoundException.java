package fr.insee.pearljam.domain.exception;

import java.io.Serial;

public class EntityNotFoundException extends Exception {
    @Serial
    private static final long serialVersionUID = -784002885484509004L;

    public EntityNotFoundException(String message) {
        super(message);
    }
}
