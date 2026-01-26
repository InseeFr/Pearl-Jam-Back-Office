package fr.insee.pearljam.domain.exception;

import java.io.Serial;

public class InterviewerNotFoundException extends RuntimeException{

    @Serial
    private static final long serialVersionUID = -784002895484809004L;

    public static final String MESSAGE = "[%s] interviewer not found";

    public InterviewerNotFoundException(String interviewerId) {
        super(MESSAGE.formatted(interviewerId));
    }


}
