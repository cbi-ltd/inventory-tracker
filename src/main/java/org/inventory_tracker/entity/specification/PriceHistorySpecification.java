package org.inventory_tracker.entity.specification;

import jakarta.persistence.criteria.Predicate;
import org.inventory_tracker.dto.request.ProductPriceHistoryFilterRequest;
import org.inventory_tracker.entity.ProductPriceHistory;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;

public class PriceHistorySpecification {

    public static Specification<ProductPriceHistory> filter(
            ProductPriceHistoryFilterRequest request) {

        return (root, query, cb) -> {

            List<Predicate> predicates = new ArrayList<>();

            if (request.getId() != null) {
                predicates.add(
                        cb.equal(root.get("id"), request.getId()));
            }

            if (request.getStationId() != null) {
                predicates.add(
                        cb.equal(
                                root.get("station").get("id"),
                                request.getStationId()));
            }

            if (request.getProductId() != null) {
                predicates.add(
                        cb.equal(
                                root.get("product").get("id"),
                                request.getProductId()));
            }

            if (request.getBusinessDate() != null) {
                predicates.add(
                        cb.equal(
                                root.get("businessDate"),
                                request.getBusinessDate()));
            }

            if (request.getChangedBy() != null &&
                    !request.getChangedBy().isBlank()) {

                predicates.add(
                        cb.like(
                                cb.lower(root.get("changedBy")),
                                "%" + request.getChangedBy().toLowerCase() + "%"));
            }

            if (request.getStartDate() != null &&
                    request.getEndDate() != null) {

                predicates.add(
                        cb.between(
                                root.get("businessDate"),
                                request.getStartDate(),
                                request.getEndDate()));
            }

            query.orderBy(
                    cb.desc(root.get("changedAt")));

            return cb.and(
                    predicates.toArray(new Predicate[0]));
        };
    }

}
