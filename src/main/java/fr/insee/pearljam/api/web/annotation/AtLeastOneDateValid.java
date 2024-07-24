package fr.insee.pearljam.api.web.annotation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Constraint(validatedBy = {AtLeastOneDateCampaignVisibilityValidator.class, AtLeastOneDateVisibilityValidator.class})
@Target({ ElementType.TYPE })
@Retention(RetentionPolicy.RUNTIME)
public @interface AtLeastOneDateValid {
    String message() default "At least one date must be provided for a visibility";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
