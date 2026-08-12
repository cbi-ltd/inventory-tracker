package org.inventory_tracker.service;

import lombok.RequiredArgsConstructor;
import org.inventory_tracker.dto.request.TerminalSyncRequest;
import org.inventory_tracker.dto.response.TerminalResponse;
import org.inventory_tracker.entity.Merchant;
import org.inventory_tracker.entity.Terminal;
import org.inventory_tracker.entity.security.MerchantContext;
import org.inventory_tracker.exception.DuplicateResourceException;
import org.inventory_tracker.exception.ResourceNotFoundException;
import org.inventory_tracker.config.mapper.TerminalMapper;
import org.inventory_tracker.repository.TerminalRepository;
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

    @Transactional
    public TerminalResponse syncTerminal(TerminalSyncRequest request) {
        Merchant merchant = getCurrentMerchant();
        Optional<Terminal> serialMatch =
                terminalRepository.findByTerminalSerialNumber(
                        request.getTerminalSerialNumber());

        if (serialMatch.isPresent() && !serialMatch.get().getTid().equals(request.getTid())) {
            throw new DuplicateResourceException("Terminal serial number already belongs to another terminal.");
        }

        Optional<Terminal> tidMatch = terminalRepository.findByTid(request.getTid());
        if (tidMatch.isPresent()) {
                Terminal existingTerminal = tidMatch.get();
                if (!existingTerminal.getMerchant().getId().equals(merchant.getId())) {
                        throw new DuplicateResourceException("Terminal already belongs to another merchant.");
                }
        }

        Terminal terminal = tidMatch.orElseGet(() -> terminalMapper.toEntity(request));
        if (terminal.getMerchant() == null) { terminal.setMerchant(merchant); }
        // Terminal terminal = terminalRepository.findByTidAndMerchant_Id(request.getTid(), merchant.getId())
        //         .orElseGet(null);
        // if (terminal == null) {
        //         terminal = terminalMapper.toEntity(request);
        //         terminal.setMerchant(merchant);
        // }

        // Terminal terminal = terminalRepository
        //         .findByTidAndMerchant_Id(request.getTid(), merchant.getId())
        //         .orElseGet(() -> terminalMapper.toEntity(request));


        terminal.setTid(request.getTid());
        terminal.setTerminalSerialNumber(request.getTerminalSerialNumber());
        terminal.setManufacturer(request.getManufacturer());
        terminal.setModel(request.getModel());
        terminal.setPosType(request.getPosType());
        terminal.setActive(request.getActive());
        terminal.setLastSyncedAt(LocalDateTime.now());

        Terminal saved = terminalRepository.save(terminal);
        return terminalMapper.toResponse(saved);
    }

    @Transactional(readOnly = true)
    public TerminalResponse getTerminalByTid(String tid) {
        Merchant merchant = getCurrentMerchant();

        Terminal terminal = terminalRepository.findByTidAndMerchant_Id(tid, merchant.getId())
                                .orElseThrow(() -> new ResourceNotFoundException("Terminal not found"));

        return terminalMapper.toResponse(terminal);
    }

    @Transactional(readOnly = true)
    public TerminalResponse getTerminalByTerminalSerialNumber(String terminalSerialNumber) {
        Merchant merchant = getCurrentMerchant();
        Terminal terminal = terminalRepository.findByTerminalSerialNumberAndMerchant_Id(terminalSerialNumber, merchant.getId())
                                .orElseThrow(() -> new ResourceNotFoundException("Terminal not found"));

        return terminalMapper.toResponse(terminal);
    }

    @Transactional(readOnly = true)
    public List<TerminalResponse> getAllTerminals() {
        Merchant merchant = getCurrentMerchant();
        return terminalMapper.toResponseList(
                terminalRepository.findByMerchant_IdOrderByTidAsc(merchant.getId())
        );
    }

    @Transactional(readOnly = true)
    public List<TerminalResponse> getActiveTerminals() {
        Merchant merchant = getCurrentMerchant();

        return terminalMapper.toResponseList(
                terminalRepository.findByMerchant_IdAndActiveTrueOrderByTidAsc(merchant.getId())
        );
    }

    private Merchant getCurrentMerchant() {
        Merchant merchant = MerchantContext.getCurrentMerchant();
        if (merchant == null) {
                throw new ResourceNotFoundException("Merchant is not authenticated");
        }
        return merchant;
    }

}