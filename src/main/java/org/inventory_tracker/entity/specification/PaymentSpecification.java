package org.inventory_tracker.entity.specification;


import jakarta.persistence.criteria.Predicate;
import org.inventory_tracker.dto.request.PaymentFilterRequest;
import org.inventory_tracker.entity.Payment;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;

public class PaymentSpecification {

    private PaymentSpecification() {
    }

    public static Specification<Payment> filter(PaymentFilterRequest request) {

        return (root, query, cb) -> {

            List<Predicate> predicates = new ArrayList<>();

            if (request.getStationId() != null) {
                predicates.add(
                        cb.equal(
                                root.get("sale")
                                        .get("station")
                                        .get("id"),
                                request.getStationId()
                        )
                );
            }

            if (request.getTerminalId() != null) {
                predicates.add(
                        cb.equal(
                                root.get("terminal")
                                        .get("id"),
                                request.getTerminalId()
                        )
                );
            }

            if (request.getSaleId() != null) {
                predicates.add(
                        cb.equal(
                                root.get("sale")
                                        .get("id"),
                                request.getSaleId()
                        )
                );
            }

            if (request.getPaymentStatus() != null) {
                predicates.add(
                        cb.equal(
                                root.get("paymentStatus"),
                                request.getPaymentStatus()
                        )
                );
            }

            if (request.getPaymentMethod() != null) {
                predicates.add(
                        cb.equal(
                                root.get("paymentMethod"),
                                request.getPaymentMethod()
                        )
                );
            }

            if (request.getPaymentNumber() != null &&
                    !request.getPaymentNumber().isBlank()) {

                predicates.add(
                        cb.equal(
                                root.get("paymentNumber"),
                                request.getPaymentNumber()
                        )
                );
            }

            if (request.getTransactionReference() != null &&
                    !request.getTransactionReference().isBlank()) {

                predicates.add(
                        cb.equal(
                                root.get("transactionReference"),
                                request.getTransactionReference()
                        )
                );
            }

            if (request.getGatewayReference() != null &&
                    !request.getGatewayReference().isBlank()) {

                predicates.add(
                        cb.equal(
                                root.get("gatewayReference"),
                                request.getGatewayReference()
                        )
                );
            }

            if (request.getProcessor() != null &&
                    !request.getProcessor().isBlank()) {

                predicates.add(
                        cb.equal(
                                root.get("processor"),
                                request.getProcessor()
                        )
                );
            }

            if (request.getPayerName() != null &&
                    !request.getPayerName().isBlank()) {

                predicates.add(
                        cb.like(
                                cb.lower(root.get("payerName")),
                                "%" + request.getPayerName().toLowerCase() + "%"
                        )
                );
            }

            if (request.getStartDate() != null) {

                predicates.add(
                        cb.greaterThanOrEqualTo(
                                root.get("paymentTime"),
                                request.getStartDate().atStartOfDay()
                        )
                );
            }

            if (request.getEndDate() != null) {

                predicates.add(
                        cb.lessThanOrEqualTo(
                                root.get("paymentTime"),
                                request.getEndDate().atTime(23, 59, 59)
                        )
                );
            }

            if (request.getMinAmount() != null) {

                predicates.add(
                        cb.greaterThanOrEqualTo(
                                root.get("amount"),
                                request.getMinAmount()
                        )
                );
            }

            if (request.getMaxAmount() != null) {

                predicates.add(
                        cb.lessThanOrEqualTo(
                                root.get("amount"),
                                request.getMaxAmount()
                        )
                );
            }

            query.orderBy(
                    cb.desc(root.get("paymentTime"))
            );

            return cb.and(
                    predicates.toArray(new Predicate[0])
            );
        };
    }
}
