package org.inventory_tracker.service;

import lombok.RequiredArgsConstructor;
import org.inventory_tracker.dto.request.TerminalSyncRequest;
import org.inventory_tracker.dto.response.TerminalResponse;
import org.inventory_tracker.entity.Station;
import org.inventory_tracker.entity.Terminal;
import org.inventory_tracker.exception.DuplicateResourceException;
import org.inventory_tracker.exception.ResourceNotFoundException;
import org.inventory_tracker.config.mapper.TerminalMapper;
import org.inventory_tracker.repository.StationRepository;
import org.inventory_tracker.repository.TerminalRepository;
import org.inventory_tracker.security.AuthenticatedUserService;
import org.inventory_tracker.security.MerchantPrincipal;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;




@Service
@RequiredArgsConstructor
public class TerminalService {
    private final TerminalRepository terminalRepository;
    private final TerminalMapper terminalMapper;
    private final AuthenticatedUserService authenticatedUserService;
    private final StationRepository stationRepository;

    @Transactional
    public TerminalResponse syncTerminal(TerminalSyncRequest request) {
        MerchantPrincipal principal = authenticatedUserService.getCurrentUser();

        Optional<Terminal> serialMatch =
                terminalRepository.findByTerminalSerialNumber(
                        request.getTerminalSerialNumber());

        Terminal terminal = terminalRepository.findByTerminalSerialNumber(request.getTerminalSerialNumber())
                .orElseGet(() -> terminalMapper.toEntity(request));
        
        if (serialMatch.isPresent()) {
                terminal = serialMatch.get();
                if (!terminal.getStation().getMerchant().getCamsMerchantId().equals(principal.getMerchantId())) {
                        throw new DuplicateResourceException("Terminal already belongs to another merchant.");
                }
        } 
        else {
                terminal = terminalMapper.toEntity(request);
        }
        
        Station station = stationRepository.findByIdAndMerchant_CamsMerchantId(request.getStationId(), principal.getMerchantId())
                                .orElseThrow(() -> new ResourceNotFoundException("Station not found"));

        Optional.ofNullable(request.getTid()).ifPresent(terminal::setTid);
        terminal.setTerminalSerialNumber(request.getTerminalSerialNumber());
        terminal.setManufacturer(request.getManufacturer());
        terminal.setStation(station);
        terminal.setModel(request.getModel());
        terminal.setPosType(request.getPosType());
        terminal.setActive(request.getActive());
        terminal.setLastSyncedAt(LocalDateTime.now());

        Terminal saved = terminalRepository.save(terminal);
        return terminalMapper.toResponse(saved);
    }

    @Transactional(readOnly = true)
    public TerminalResponse getTerminalByTid(String tid) {
        MerchantPrincipal principal = authenticatedUserService.getCurrentUser();
        Terminal terminal =
                terminalRepository.findByTidAndStation_Merchant_CamsMerchantId(tid, principal.getMerchantId())
                        .orElseThrow(() -> new ResourceNotFoundException("Terminal not found"));

        return terminalMapper.toResponse(terminal);
    }

    @Transactional(readOnly = true)
    public TerminalResponse getTerminalByTerminalSerialNumber(String terminalSerialNumber) {
        MerchantPrincipal principal = authenticatedUserService.getCurrentUser();
        Terminal terminal =
                terminalRepository.findByTerminalSerialNumberAndStation_Merchant_CamsMerchantId(
                                terminalSerialNumber, principal.getMerchantId())
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Terminal not found"));

        return terminalMapper.toResponse(terminal);
    }

    @Transactional(readOnly = true)
    public List<TerminalResponse> getAllTerminals() {
        MerchantPrincipal principal = authenticatedUserService.getCurrentUser();
        return terminalMapper.toResponseList(
                terminalRepository.findByStation_Merchant_CamsMerchantIdOrderByTidAsc(principal.getMerchantId())
        );
    }

    @Transactional(readOnly = true)
    public List<TerminalResponse> getActiveTerminals() {
        MerchantPrincipal principal = authenticatedUserService.getCurrentUser();
        return terminalMapper.toResponseList(
                terminalRepository.findByStation_Merchant_CamsMerchantIdAndActiveTrueOrderByTidAsc(principal.getMerchantId())
        );
    }

    private Terminal getOrCreateTerminal(String terminalSerialNumber, String tid, Station station) {
        Terminal terminal = terminalRepository.findByTerminalSerialNumber(terminalSerialNumber)
                                        .orElseGet(Terminal::new);

        terminal.setTerminalSerialNumber(terminalSerialNumber);
        terminal.setTid(tid);
        terminal.setStation(station);
        terminal.setLastSyncedAt(LocalDateTime.now());

        return terminalRepository.save(terminal);
    }
}