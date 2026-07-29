package org.inventory_tracker.entity;


import jakarta.persistence.criteria.Predicate;
import org.inventory_tracker.dto.request.DeliveryFilterRequest;
import org.springframework.data.jpa.domain.Specification;
import java.util.ArrayList;
import java.util.List;

public class DeliverySpecification {

    private DeliverySpecification() {
    }

    public static Specification<Delivery> filter(DeliveryFilterRequest request) {

        return (root, query, cb) -> {

            List<Predicate> predicates = new ArrayList<>();

            if (request.getDeliveryNumber() != null &&
                    !request.getDeliveryNumber().isBlank()) {

                predicates.add(
                        cb.equal(
                                root.get("deliveryNumber"),
                                request.getDeliveryNumber()
                        )
                );
            }

            if (request.getStationId() != null) {

                predicates.add(
                        cb.equal(
                                root.get("station").get("id"),
                                request.getStationId()
                        )
                );
            }

            if (request.getProductId() != null) {

                predicates.add(
                        cb.equal(
                                root.get("product").get("id"),
                                request.getProductId()
                        )
                );
            }

            if (request.getStationInventoryId() != null) {

                predicates.add(
                        cb.equal(
                                root.get("stationInventory").get("id"),
                                request.getStationInventoryId()
                        )
                );
            }

            if (request.getStatus() != null) {

                predicates.add(
                        cb.equal(
                                root.get("status"),
                                request.getStatus()
                        )
                );
            }

            if (request.getStartDate() != null &&
                    request.getEndDate() != null) {

                predicates.add(
                        cb.between(
                                root.get("businessDate"),
                                request.getStartDate(),
                                request.getEndDate()
                        )
                );
            }

            if (request.getStartDate() != null &&
                    request.getEndDate() == null) {

                predicates.add(
                        cb.greaterThanOrEqualTo(
                                root.get("businessDate"),
                                request.getStartDate()
                        )
                );
            }

            if (request.getStartDate() == null &&
                    request.getEndDate() != null) {

                predicates.add(
                        cb.lessThanOrEqualTo(
                                root.get("businessDate"),
                                request.getEndDate()
                        )
                );
            }

            query.orderBy(

                    cb.desc(root.get("businessDate")),

                    cb.desc(root.get("receivedAt"))
            );

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }
}
