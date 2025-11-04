package fr.insee.pearljam.shared.exception;

import java.io.Serial;

public class EntityAlreadyExistException extends Exception {
    @Serial
    private static final long serialVersionUID = -478002885484590045L;

    public EntityAlreadyExistException(String message) {
        super(message);
    }
}
