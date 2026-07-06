package org.inventory_tracker.service;

import lombok.RequiredArgsConstructor;
import org.inventory_tracker.entity.Pump;
import org.inventory_tracker.enums.ProductType;
import org.inventory_tracker.repository.PumpRepository;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PumpService {

    private final PumpRepository pumpRepository;

    public Pump createPump(String outletId,
                           Integer pumpNumber,
                           String name,
                            ProductType productType) {

        Pump pump = new Pump();
        pump.setOutletId(outletId);
        pump.setPumpNumber(pumpNumber);
        pump.setName(name);
        pump.setProductType(productType);
        pump.setActive(true);

        return pumpRepository.save(pump);
    }

    public Pump activate(Long pumpId) {
        Pump pump = pumpRepository.findById(pumpId)
                .orElseThrow(() -> new RuntimeException("Pump not found"));

        pump.setActive(true);
        return pumpRepository.save(pump);
    }

    public Pump assignToStaff(Long pumpId, String staffId) {

        Pump pump = pumpRepository.findById(pumpId)
                .orElseThrow(() -> new RuntimeException("Pump not found"));

        // No DB relation needed (CAMS owns Staff)
        pump.setAssignedStaffId(staffId);
        return pumpRepository.save(pump);
    }

    public Pump deactivate(Long pumpId) {
        Pump pump = pumpRepository.findById(pumpId)
                .orElseThrow(() -> new RuntimeException("Pump not found"));

        pump.setActive(false);
        return pumpRepository.save(pump);
    }
}
