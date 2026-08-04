package org.inventory_tracker.entity.specification;


import jakarta.persistence.criteria.Predicate;
import org.inventory_tracker.dto.request.PumpAuditFilterRequest;
import org.inventory_tracker.entity.PumpAudit;
import org.springframework.data.jpa.domain.Specification;
import java.util.ArrayList;
import java.util.List;

public class PumpAuditSpecification {

    public static Specification<PumpAudit> filter(PumpAuditFilterRequest request) {

        return (root, query, cb) -> {

            List<Predicate> predicates = new ArrayList<>();

            if (request.getId() != null) {
                predicates.add(
                        cb.equal(root.get("id"), request.getId())
                );
            }

            if (request.getAssignmentId() != null) {
                predicates.add(
                        cb.equal(
                                root.get("pumpAssignment").get("id"),
                                request.getAssignmentId()
                        )
                );
            }

            if (request.getStationId() != null) {
                predicates.add(
                        cb.equal(
                                root.get("pumpAssignment").get("station").get("id"),
                                request.getStationId()
                        )
                );
            }

            if (request.getPumpId() != null) {
                predicates.add(
                        cb.equal(
                                root.get("pumpAssignment").get("pump").get("id"),
                                request.getPumpId()
                        )
                );
            }

            if (request.getAttendantId() != null) {
                predicates.add(
                        cb.equal(
                                root.get("pumpAssignment").get("attendant").get("id"),
                                request.getAttendantId()
                        )
                );
            }

            if (request.getBusinessDate() != null) {
                predicates.add(
                        cb.equal(
                                root.get("businessDate"),
                                request.getBusinessDate()
                        )
                );
            }

            query.orderBy(
                    cb.desc(root.get("businessDate")),
                    cb.desc(root.get("clockInTime"))
            );

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }

}
