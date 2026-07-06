package org.inventory_tracker.service;


import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.inventory_tracker.config.mapper.PumpAuditMapper;
import org.inventory_tracker.dto.request.CreatePumpAuditRequest;
import org.inventory_tracker.dto.request.CreatePumpRequest;
import org.inventory_tracker.dto.response.PumpAuditResponse;
import org.inventory_tracker.dto.response.PumpResponse;
import org.inventory_tracker.entity.*;
import org.inventory_tracker.repository.*;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PumpAuditService {

    private final PumpAuditRepository auditRepository;
    private final PumpRepository pumpRepository;
    private final StationRepository stationRepository;
    private final AttendantRepository attendantRepository;
    private final PosTerminalRepository posTerminalRepository;
    private final PumpAuditMapper pumpAuditMapper;

    public PumpAudit openShift(Long pumpId,
                               String outletId,
                               String merchantId,
                               String staffId,
                               String posTerminalId,
                               Double openingReading) {

        Pump pump = pumpRepository.findById(pumpId)
                .orElseThrow(() -> new RuntimeException("Pump not found"));

        PumpAudit audit = new PumpAudit();
        audit.setPump(pump);
        audit.setOutletId(outletId);
        audit.setMerchantId(merchantId);
        audit.setAttendantId(staffId);
        // audit.setPosTerminalId(posTerminalId);
        audit.setOpeningReading(openingReading);

        return auditRepository.save(audit);
    }

    public PumpAudit closeShift(Long auditId, Double closingReading) {

        PumpAudit audit = auditRepository.findById(auditId)
                .orElseThrow(() -> new RuntimeException("Audit not found"));

        audit.setClosingReading(closingReading);

        double dispensed = closingReading - audit.getOpeningReading();
        audit.setTotalDispensed(dispensed);

        return auditRepository.save(audit);
    }

        public PumpAuditResponse create(CreatePumpAuditRequest request) {
        Station station = stationRepository.findById(request.getStationId())
                .orElseThrow(() -> new RuntimeException("Station not found"));

        Attendant attendant = attendantRepository.findById(request.getAttendantId())
                .orElseThrow(() -> new RuntimeException("Attendant not found"));

        PosTerminal terminal = posTerminalRepository.findById(request.getPosTerminalId())
                .orElseThrow(() -> new RuntimeException("POS terminal not found"));

        PumpAudit audit = pumpAuditMapper.toEntity(request);
        audit.setStation(station);
        // audit.setAttendant(attendant);
        audit.setPosTerminal(terminal);

        PumpAudit savedAudit = auditRepository.save(audit);
        return pumpAuditMapper.toResponse(savedAudit);
    }

    public List<PumpAuditResponse> getAll() {
        List<PumpAudit> audits = auditRepository.findAll();
        return pumpAuditMapper.toResponseList(audits);
    }

    public PumpAuditResponse getById(Long id) {
        PumpAudit audit = auditRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Pump audit not found"));

        return pumpAuditMapper.toResponse(audit);
    }

    public PumpAuditResponse update(Long id, CreatePumpAuditRequest request) {

        PumpAudit audit = auditRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Pump audit not found"));

        Station station = stationRepository.findById(request.getStationId())
                .orElseThrow(() -> new RuntimeException("Station not found"));

        Attendant attendant = attendantRepository.findById(request.getAttendantId())
                .orElseThrow(() -> new RuntimeException("Attendant not found"));

        PosTerminal terminal = posTerminalRepository.findById(request.getPosTerminalId())
                .orElseThrow(() -> new RuntimeException("POS terminal not found"));

        audit.setPump(request.getPump());
        audit.setOpeningReading(request.getOpeningReading());
        audit.setClosingReading(request.getClosingReading());
        audit.setStation(station);
        // audit.setAttendant(attendant);
        audit.setPosTerminal(terminal);

        PumpAudit updatedAudit = auditRepository.save(audit);
        return pumpAuditMapper.toResponse(updatedAudit);
    }

    @Transactional
    public PumpResponse createPump(CreatePumpRequest request) {
        Station station = stationRepository
                .findById(request.getStationId())
                .orElseThrow(() -> new RuntimeException("Station not found"));

        Pump pump = new Pump();
        pump.setPumpNumber(request.getPumpNumber());
        pump.setStation(station);
        pump.setActive(true);

        pump = pumpRepository.save(pump);
        return PumpResponse.builder()
                .id(pump.getId())
                .pumpNumber(pump.getPumpNumber())
                .build();
    }

    public void delete(Long id) {
        PumpAudit audit = auditRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Pump audit not found"));

        auditRepository.delete(audit);
    }
}








//import jakarta.transaction.Transactional;
//import lombok.RequiredArgsConstructor;
//import org.inventory_tracker.dto.request.CreatePumpAuditRequest;
//import org.inventory_tracker.dto.request.CreatePumpRequest;
//import org.inventory_tracker.dto.response.PumpAuditResponse;
//import org.inventory_tracker.dto.response.PumpResponse;
//import org.inventory_tracker.entity.*;
//import org.inventory_tracker.config.mapper.PumpAuditMapper;
//import org.inventory_tracker.repository.*;
//import org.springframework.stereotype.Service;
//
//import java.util.List;
//
//@Service
//@RequiredArgsConstructor
//public class PumpAuditService {
//
//    private final PumpAuditRepository pumpAuditRepository;
//    private final PumpRepository pumpRepository;
//    private final StationRepository stationRepository;
//    private final AttendantRepository attendantRepository;
//    private final PosTerminalRepository posTerminalRepository;
//    private final PumpAuditMapper pumpAuditMapper;
//
//    public PumpAuditResponse create(CreatePumpAuditRequest request) {
//        Station station = stationRepository.findById(request.getStationId())
//                .orElseThrow(() -> new RuntimeException("Station not found"));
//
//        Attendant attendant = attendantRepository.findById(request.getAttendantId())
//                .orElseThrow(() -> new RuntimeException("Attendant not found"));
//
//        PosTerminal terminal = posTerminalRepository.findById(request.getPosTerminalId())
//                .orElseThrow(() -> new RuntimeException("POS terminal not found"));
//
//        PumpAudit audit = pumpAuditMapper.toEntity(request);
//        audit.setStation(station);
//        audit.setAttendant(attendant);
//        audit.setPosTerminal(terminal);
//
//        PumpAudit savedAudit = pumpAuditRepository.save(audit);
//        return pumpAuditMapper.toResponse(savedAudit);
//    }
//
//    public List<PumpAuditResponse> getAll() {
//        List<PumpAudit> audits = pumpAuditRepository.findAll();
//        return pumpAuditMapper.toResponseList(audits);
//    }
//
//    public PumpAuditResponse getById(Long id) {
//        PumpAudit audit = pumpAuditRepository.findById(id)
//                .orElseThrow(() -> new RuntimeException("Pump audit not found"));
//
//        return pumpAuditMapper.toResponse(audit);
//    }
//
//    public PumpAuditResponse update(Long id, CreatePumpAuditRequest request) {
//
//        PumpAudit audit = pumpAuditRepository.findById(id)
//                .orElseThrow(() -> new RuntimeException("Pump audit not found"));
//
//        Station station = stationRepository.findById(request.getStationId())
//                .orElseThrow(() -> new RuntimeException("Station not found"));
//
//        Attendant attendant = attendantRepository.findById(request.getAttendantId())
//                .orElseThrow(() -> new RuntimeException("Attendant not found"));
//
//        PosTerminal terminal = posTerminalRepository.findById(request.getPosTerminalId())
//                .orElseThrow(() -> new RuntimeException("POS terminal not found"));
//
//        audit.setPump(request.getPump());
//        audit.setOpeningReading(request.getOpeningReading());
//        audit.setClosingReading(request.getClosingReading());
//        audit.setStation(station);
//        audit.setAttendant(attendant);
//        audit.setPosTerminal(terminal);
//
//        PumpAudit updatedAudit = pumpAuditRepository.save(audit);
//        return pumpAuditMapper.toResponse(updatedAudit);
//    }
//
//    @Transactional
//    public PumpResponse createPump(CreatePumpRequest request) {
//        Station station = stationRepository
//                .findById(request.getStationId())
//                .orElseThrow(() -> new RuntimeException("Station not found"));
//
//        Pump pump = new Pump();
//        pump.setPumpNumber(request.getPumpNumber());
//        pump.setStation(station);
//        pump.setActive(true);
//
//        pump = pumpRepository.save(pump);
//        return PumpResponse.builder()
//                .id(pump.getId())
//                .pumpNumber(pump.getPumpNumber())
//                .build();
//    }
//
//    public void delete(Long id) {
//        PumpAudit audit = pumpAuditRepository.findById(id)
//                .orElseThrow(() -> new RuntimeException("Pump audit not found"));
//
//        pumpAuditRepository.delete(audit);
//    }
//}