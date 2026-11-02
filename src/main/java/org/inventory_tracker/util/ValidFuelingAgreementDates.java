package org.inventory_tracker.util;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = FuelingAgreementDatesValidator.class)
@Documented
public @interface ValidFuelingAgreementDates {

    String message() default "Invalid fueling agreement dates";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
