package org.inventory_tracker.util;

import org.inventory_tracker.dto.request.CreateFuelingAgreementRequest;
import org.inventory_tracker.enums.FuelingSettlementType;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;


public class FuelingAgreementDatesValidator
        implements ConstraintValidator<ValidFuelingAgreementDates, CreateFuelingAgreementRequest> {

    @Override
    public boolean isValid(CreateFuelingAgreementRequest request, ConstraintValidatorContext context) {

        if (request == null || request.getSettlementType() == null) {
            return true;
        }

        if (request.getSettlementType() == FuelingSettlementType.POSTPAID
                && request.getEndDate() == null) {

            context.disableDefaultConstraintViolation();

            context.buildConstraintViolationWithTemplate("End date is required for postpaid agreements")
                .addPropertyNode("endDate")
                .addConstraintViolation();

            return false;
        }

        return true;
    }
}
