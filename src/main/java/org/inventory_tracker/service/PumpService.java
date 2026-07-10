package org.inventory_tracker.service;

import lombok.RequiredArgsConstructor;

import org.inventory_tracker.repository.PumpRepository;
import org.inventory_tracker.repository.StationRepository;
import org.inventory_tracker.config.mapper.PumpMapper;
import org.inventory_tracker.dto.request.CreatePumpRequest;
import org.inventory_tracker.dto.response.PumpResponse;
import org.inventory_tracker.exception.DuplicateResourceException;
import org.inventory_tracker.exception.ResourceNotFoundException;
import org.springframework.transaction.annotation.Transactional;
import org.inventory_tracker.repository.ProductRepository;
import java.util.List;
import org.inventory_tracker.entity.*;
import org.springframework.stereotype.Service;
import org.inventory_tracker.repository.TerminalRepository;

@Service
@RequiredArgsConstructor
public class PumpService {

    private final PumpRepository pumpRepository;
    private final StationRepository stationRepository;
    private final ProductRepository productRepository;
    private final TerminalRepository terminalRepository;
    private final PumpMapper pumpMapper;

    @Transactional
    public PumpResponse createPump(CreatePumpRequest request) {

        Station station = stationRepository.findById(request.getStationId())
                .orElseThrow(() ->
                        new ResourceNotFoundException("Station not found"));
        Product product = productRepository.findById(request.getProductId())
        .orElseThrow(() -> new ResourceNotFoundException("Product not found"));

        if (pumpRepository.existsByPumpNumberAndStation_Id(
                request.getPumpNumber(),
                station.getId())) {

            throw new DuplicateResourceException(
                    "Pump number already exists in this station");
        }

        Pump pump = pumpMapper.toEntity(request);
        pump.setStation(station);
        pump.setProduct(product);
        Pump savedPump = pumpRepository.save(pump);

        return pumpMapper.toResponse(savedPump);
    }

    @Transactional(readOnly = true)
    public PumpResponse getPumpById(Long id) {

        Pump pump = pumpRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Pump not found"));

        return pumpMapper.toResponse(pump);
    }

    @Transactional
    public PumpResponse updatePump(Long id,
                                   CreatePumpRequest request) {

        Pump pump = pumpRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Pump not found"));

        Station station = stationRepository.findById(request.getStationId())
                .orElseThrow(() ->
                        new ResourceNotFoundException("Station not found"));

        Product product = productRepository.findById(request.getProductId())
        .orElseThrow(() -> new ResourceNotFoundException("Product not found"));

        // if (!pump.getPumpNumber().equals(request.getPumpNumber())
        //         &&
        //         pumpRepository.existsByPumpNumberAndStation_Id(
        //                 request.getPumpNumber(),
        //                 station.getId())) {

        //     throw new DuplicateResourceException(
        //             "Pump number already exists in this station");
        // }

        if (pumpRepository.existsByPumpNumberAndStation_IdAndIdNot(request.getPumpNumber(), request.getStationId(), id)) {
                throw new DuplicateResourceException("Pump number already exists in this station");
        }

        pump.setPumpNumber(request.getPumpNumber());
        pump.setPumpName(request.getPumpName());
        Terminal terminal = terminalRepository.findById(request.getDefaultTerminalId())
                                .orElseThrow(() -> new ResourceNotFoundException("Terminal not found"));

        pump.setDefaultTerminal(terminal);
        // pump.setDefaultTerminalId(request.getTerminalId());
        // pump.setDefaultTerminalSerialNumber(request.getTerminalSerialNumber());
        pump.setProduct(product);
        pump.setStation(station);

        Pump updatedPump = pumpRepository.save(pump);

        return pumpMapper.toResponse(updatedPump);
    }

    @Transactional(readOnly = true)
    public List<PumpResponse> getAllPumps() {

        return pumpMapper.toResponseList(
                pumpRepository.findAllByOrderByPumpNumberAsc()
        );
    }

    @Transactional(readOnly = true)
    public List<PumpResponse> getPumpsByStation(Long stationId) {

        if (!stationRepository.existsById(stationId)) {
            throw new ResourceNotFoundException("Station not found");
        }

        return pumpMapper.toResponseList(
                pumpRepository.findByStation_IdOrderByPumpNumberAsc(stationId)
        );
    }

    @Transactional(readOnly = true)
    public List<PumpResponse> getActivePumps() {

        return pumpMapper.toResponseList(
                pumpRepository.findByActiveTrueOrderByPumpNumberAsc()
        );
    }

    @Transactional
    public PumpResponse activatePump(Long id) {

        Pump pump = pumpRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Pump not found"));

        if (Boolean.TRUE.equals(pump.getActive())) {
            throw new DuplicateResourceException(
                    "Pump is already active");
        }

        pump.setActive(true);

        Pump updatedPump = pumpRepository.save(pump);

        return pumpMapper.toResponse(updatedPump);
    }

    @Transactional
    public PumpResponse deactivatePump(Long id) {

        Pump pump = pumpRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Pump not found"));

        if (Boolean.FALSE.equals(pump.getActive())) {
            throw new DuplicateResourceException(
                    "Pump is already inactive");
        }

        pump.setActive(false);

        Pump updatedPump = pumpRepository.save(pump);

        return pumpMapper.toResponse(updatedPump);
    }
}