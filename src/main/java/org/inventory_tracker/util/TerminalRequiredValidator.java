package org.inventory_tracker.util;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.inventory_tracker.dto.request.CreatePumpRequest;

public class TerminalRequiredValidator implements ConstraintValidator<ValidTerminalInfo, CreatePumpRequest> {

    @Override
    public boolean isValid(CreatePumpRequest request, ConstraintValidatorContext context) {
        if (request == null) {
            return true;
        }

        boolean hasTerminalId = request.getDefaultTerminalId() != null;
        boolean hasSerialNumber = request.getTerminalSerialNumber() != null && !request.getTerminalSerialNumber().trim().isEmpty();

        // Returns true if at least one is present
        return hasTerminalId || hasSerialNumber; 
    }
}

