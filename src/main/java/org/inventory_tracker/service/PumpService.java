package org.inventory_tracker.service;

import lombok.RequiredArgsConstructor;

import org.inventory_tracker.repository.PumpRepository;
import org.inventory_tracker.repository.StationRepository;
import org.inventory_tracker.config.mapper.PumpMapper;
import org.inventory_tracker.dto.request.CreatePumpRequest;
import org.inventory_tracker.dto.request.UpdatePumpRequest;
import org.inventory_tracker.dto.response.PumpResponse;
import org.inventory_tracker.exception.DuplicateResourceException;
import org.inventory_tracker.exception.ResourceNotFoundException;
import org.inventory_tracker.exception.BadRequestException;
import org.springframework.transaction.annotation.Transactional;
import org.inventory_tracker.repository.ProductRepository;
import java.util.List;
import org.inventory_tracker.entity.*;
import org.inventory_tracker.enums.TerminalMode;
import org.springframework.stereotype.Service;
import org.inventory_tracker.repository.TerminalRepository;
import org.inventory_tracker.security.AuthenticatedUserService;

@Service
@RequiredArgsConstructor
public class PumpService {

    private final PumpRepository pumpRepository;
    private final StationRepository stationRepository;
    private final ProductRepository productRepository;
    private final TerminalRepository terminalRepository;
    private final PumpMapper pumpMapper;
    private final AuthenticatedUserService authenticatedUserService;

    // @Transactional
    // public PumpResponse createPump(CreatePumpRequest request) {
    //     Merchant merchant = authenticatedUserService.getCurrentMerchant();
    //     Station station = getMerchantStation(request.getStationId(), merchant);
    //     // Station station = stationRepository.findById(request.getStationId())
    //     //         .orElseThrow(() -> new ResourceNotFoundException("Station not found"));

    //     Product product = productRepository.findById(request.getProductId())
    //                             .orElseThrow(() -> new ResourceNotFoundException("Product not found"));

    //     if (pumpRepository.existsByPumpNumberAndStation_Id(request.getPumpNumber(), station.getId())) {
    //         throw new DuplicateResourceException("Pump number already exists in this station");
    //     }

    //     Pump pump = pumpMapper.toEntity(request);
    //     pump.setStation(station);
    //     pump.setProduct(product);

    //     if (request.getDefaultTerminalId() != null) {
    //         Terminal terminal = terminalRepository.findByIdAndStation_Merchant_Id(request.getDefaultTerminalId(), merchant.getId())
    //                                 .orElseThrow(() ->new ResourceNotFoundException("Terminal not found"));

    //         if (!terminal.getStation().getId().equals(station.getId())) {
    //             throw new BadRequestException("Terminal does not belong to the selected station");
    //         }

    //         pump.setDefaultTerminal(terminal);
    //     }

    //     Pump savedPump = pumpRepository.save(pump);
    //     return pumpMapper.toResponse(savedPump);
    // }


    @Transactional
    public PumpResponse createPump(CreatePumpRequest request) {
        Merchant merchant = authenticatedUserService.getCurrentMerchant();
        Station station = getMerchantStation(request.getStationId(), merchant);
        // Station station = stationRepository.findById(request.getStationId())
        //         .orElseThrow(() -> new ResourceNotFoundException("Station not found"));

        Product product = productRepository.findById(request.getProductId())
                                .orElseThrow(() -> new ResourceNotFoundException("Product not found"));

        if (pumpRepository.existsByPumpNumberAndStation_Id(request.getPumpNumber(), station.getId())) {
            throw new DuplicateResourceException("Pump number already exists in this station");
        }

        Pump pump = pumpMapper.toEntity(request);
        pump.setStation(station);
        pump.setProduct(product);

        if (request.getDefaultTerminalId() != null) {
            Terminal terminal = terminalRepository.findByIdAndStation_Merchant_Id(
                            request.getDefaultTerminalId(), merchant.getId())
                                .orElseThrow(() -> new ResourceNotFoundException("Terminal not found"));

            validateTerminalAssignment(station, terminal, null);
            pump.setDefaultTerminal(terminal);
        }

        Pump savedPump = pumpRepository.save(pump);
        return pumpMapper.toResponse(savedPump);
    }


    @Transactional(readOnly = true)
    public PumpResponse getPumpById(Long id) {
        Merchant merchant = authenticatedUserService.getCurrentMerchant();
        Pump pump = pumpRepository.findByIdAndStation_Merchant_Id(id, merchant.getId())
                                .orElseThrow(() ->new ResourceNotFoundException("Pump not found"));

        return pumpMapper.toResponse(pump);
    }

    @Transactional
    public PumpResponse updatePump(Long id, UpdatePumpRequest request) {
        Merchant merchant = authenticatedUserService.getCurrentMerchant();
        Pump pump = pumpRepository.findByIdAndStation_Merchant_Id(id, merchant.getId())
                .orElseThrow(() ->
                        new ResourceNotFoundException("Pump not found"));

        Long targetStationId = request.getStationId() != null
                    ? request.getStationId()
                    : pump.getStation().getId();

        Station targetStation = getMerchantStation(targetStationId, merchant);

        if (request.getPumpNumber() != null && 
                pumpRepository.existsByPumpNumberAndStation_IdAndIdNot(
                        request.getPumpNumber(), 
                        request.getStationId() != null ? request.getStationId() : pump.getStation().getId(), 
                        id)) {
                throw new DuplicateResourceException("Pump number already exists in this station");
        }

        pumpMapper.updatePumpFromDto(request, pump);
        pump.setStation(targetStation);

        if (request.getStationId() != null && !targetStation.getId().equals(pump.getDefaultTerminal() != null
                ? pump.getDefaultTerminal().getStation().getId() : null)) {

                pump.setDefaultTerminal(null);
        }

        if (request.getProductId() != null) {
                Product product = productRepository.findById(request.getProductId())
                        .orElseThrow(() ->
                                new ResourceNotFoundException("Product not found"));

                pump.setProduct(product);
        }

        if (request.getDefaultTerminalId() != null) {
            Terminal terminal = terminalRepository
                    .findByIdAndStation_Merchant_Id(request.getDefaultTerminalId(), merchant.getId())
                    .orElseThrow(() -> new ResourceNotFoundException("Terminal not found"));

            validateTerminalAssignment(targetStation, terminal, pump.getId());
            pump.setDefaultTerminal(terminal);
        }

        Pump updatedPump = pumpRepository.save(pump);

        return pumpMapper.toResponse(updatedPump);
    }

    @Transactional(readOnly = true)
    public List<PumpResponse> getAllPumps() {
        Merchant merchant = authenticatedUserService.getCurrentMerchant();
        return pumpMapper.toResponseList(pumpRepository.findByStation_Merchant_IdOrderByPumpNumberAsc(merchant.getId()));
        // return pumpMapper.toResponseList(
        //         pumpRepository.findAllByOrderByPumpNumberAsc()
        // );
    }

    @Transactional(readOnly = true)
    public List<PumpResponse> getPumpsByStation(Long stationId) {
        Merchant merchant = authenticatedUserService.getCurrentMerchant();
        Station station = getMerchantStation(stationId, merchant);

        return pumpMapper.toResponseList(pumpRepository.findByStation_IdOrderByPumpNumberAsc(station.getId()));
    }

    @Transactional(readOnly = true)
    public List<PumpResponse> getActivePumps() {
        Merchant merchant = authenticatedUserService.getCurrentMerchant();
        return pumpMapper.toResponseList(pumpRepository
                        .findByStation_Merchant_IdAndActiveTrueOrderByPumpNumberAsc(merchant.getId()));
    }

    @Transactional
    public PumpResponse activatePump(Long id) {
        Merchant merchant = authenticatedUserService.getCurrentMerchant();
        Pump pump = pumpRepository.findByIdAndStation_Merchant_Id(id, merchant.getId())
                                        .orElseThrow(() -> new ResourceNotFoundException("Pump not found"));

        if (Boolean.TRUE.equals(pump.getActive())) {
                throw new DuplicateResourceException("Pump is already active");
        }

        pump.setActive(true);
        Pump updatedPump = pumpRepository.save(pump);

        return pumpMapper.toResponse(updatedPump);
    }

    @Transactional
    public PumpResponse deactivatePump(Long id) {
        Merchant merchant = authenticatedUserService.getCurrentMerchant();

        Pump pump = pumpRepository.findByIdAndStation_Merchant_Id(id, merchant.getId())
                        .orElseThrow(() -> new ResourceNotFoundException("Pump not found"));

        if (Boolean.FALSE.equals(pump.getActive())) {
            throw new DuplicateResourceException("Pump is already inactive");
        }

        pump.setActive(false);
        Pump updatedPump = pumpRepository.save(pump);
        return pumpMapper.toResponse(updatedPump);
    }

    // private Merchant getCurrentMerchant() {
    //     Merchant merchant = MerchantContext.getCurrentMerchant();
    //     if (merchant == null) { throw new ResourceNotFoundException("Merchant is not authenticated"); }
    //     return merchant;
    // }

   private Station getMerchantStation(Long stationId, Merchant merchant) {
        return stationRepository.findByIdAndMerchantId(stationId, merchant.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Station not found"));
   }

   private void validateTerminalAssignment(
        Station station,
        Terminal terminal,
        Long pumpId) {

    if (terminal == null) {
        return;
    }

    // Terminal must belong to the selected station
    if (!terminal.getStation().getId().equals(station.getId())) {
        throw new BadRequestException(
                "Terminal does not belong to the selected station"
        );
    }

    // Multi-pump stations allow multiple pumps to share a terminal
    if (station.getTerminalMode() == TerminalMode.MULTI_PUMP) {
        return;
    }

    // Single-pump station:
    // check whether another pump is already using this terminal
    boolean alreadyAssigned;

    if (pumpId == null) {

        // Creating a new pump
        alreadyAssigned =
                pumpRepository.existsByStation_IdAndDefaultTerminal_Id(
                        station.getId(),
                        terminal.getId()
                );

    } else {

        // Updating an existing pump
        alreadyAssigned =
                pumpRepository.existsByStation_IdAndDefaultTerminal_IdAndIdNot(
                        station.getId(),
                        terminal.getId(),
                        pumpId
                );
    }

    if (alreadyAssigned) {
        throw new DuplicateResourceException(
                "Terminal " + terminal.getTerminalSerialNumber()
                        + " is already assigned to another pump in this station"
        );
    }
}


}