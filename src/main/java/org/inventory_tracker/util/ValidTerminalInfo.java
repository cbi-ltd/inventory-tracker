package org.inventory_tracker.util;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.*;

@Target({ElementType.TYPE}) // Targets the class level
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = TerminalRequiredValidator.class)
@Documented
public @interface ValidTerminalInfo {
    String message() default "You have to give me either defaultTerminalId or terminalSerialNumber";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}

