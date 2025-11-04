package fr.insee.pearljam.campaign.infrastructure.rest.annotation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Constraint(validatedBy = {NoDuplicateMediumAndTypeValidator.class})
@Target({ElementType.METHOD, ElementType.FIELD, ElementType.CONSTRUCTOR, ElementType.PARAMETER, ElementType.TYPE_USE})
@Retention(RetentionPolicy.RUNTIME)
public @interface NoDuplicateMediumAndType {
    String message() default "Some communication configurations have same type and medium, that should not be possible";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
