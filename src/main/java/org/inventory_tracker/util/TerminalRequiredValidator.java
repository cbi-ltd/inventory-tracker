package org.inventory_tracker.util;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import org.inventory_tracker.dto.request.ClosePumpAuditRequest;
import org.inventory_tracker.dto.request.CreatePumpRequest;

public class TerminalRequiredValidator implements ConstraintValidator<ValidTerminalInfo, Object> {

    // @Override
    // public boolean isValid(CreatePumpRequest request, ConstraintValidatorContext context) {
    //     if (request == null) {
    //         return true;
    //     }

    //     boolean hasTerminalId = request.getDefaultTerminalId() != null;
    //     boolean hasSerialNumber = request.getTerminalSerialNumber() != null && !request.getTerminalSerialNumber().trim().isEmpty();

    //     // Returns true if at least one is present
    //     return hasTerminalId || hasSerialNumber; 
    // }

    @Override
    public boolean isValid(Object request, ConstraintValidatorContext context) {
        if (request == null) {
            return true;
        }

        if (request instanceof CreatePumpRequest createPumpRequest) {
            return isValidCreatePumpRequest(createPumpRequest);
        }

        if (request instanceof ClosePumpAuditRequest closePumpAuditRequest) {
            return isValidClosePumpAuditRequest(closePumpAuditRequest, context);
        }

        return true;
    }

    private boolean isValidCreatePumpRequest(CreatePumpRequest request) {
        boolean hasTerminalId = request.getDefaultTerminalId() != null;

        boolean hasSerialNumber = request.getTerminalSerialNumber() != null && !request.getTerminalSerialNumber().trim().isEmpty();

        return hasTerminalId || hasSerialNumber;
    }

    private boolean isValidClosePumpAuditRequest(ClosePumpAuditRequest request, ConstraintValidatorContext context) {
        boolean hasTerminalSerialNumber = request.getTerminalSerialNumber() != null
                        && !request.getTerminalSerialNumber().trim().isEmpty();

        boolean hasPumpAssignmentId = request.getPumpAssignmentId() != null;

        /*
         * XOR:
         *
         * true  + false = valid
         * false + true  = valid
         * false + false = invalid
         * true  + true  = invalid
         */
        boolean valid = hasTerminalSerialNumber ^ hasPumpAssignmentId;

        if (!valid) {
            context.disableDefaultConstraintViolation();

            context.buildConstraintViolationWithTemplate("Provide exactly one of terminalSerialNumber or pumpAssignmentId")
                    .addConstraintViolation();
        }

        return valid;
    }
}

