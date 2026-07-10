package org.inventory_tracker.service;

import lombok.RequiredArgsConstructor;
import org.inventory_tracker.dto.request.TerminalSyncRequest;
import org.inventory_tracker.dto.response.TerminalResponse;
import org.inventory_tracker.entity.Terminal;
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

        Optional<Terminal> serialMatch =
                terminalRepository.findByTerminalSerialNumber(
                        request.getTerminalSerialNumber());

        if (serialMatch.isPresent()
                && !serialMatch.get().getTerminalId()
                .equals(request.getTerminalId())) {

            throw new DuplicateResourceException(
                    "Terminal serial number already belongs to another terminal."
            );
        }

        Terminal terminal = terminalRepository
                .findByTerminalId(request.getTerminalId())
                .orElseGet(() -> terminalMapper.toEntity(request));

        terminal.setTerminalId(request.getTerminalId());
        terminal.setTerminalSerialNumber(request.getTerminalSerialNumber());
        terminal.setManufacturer(request.getManufacturer());
        terminal.setModel(request.getModel());
        terminal.setPosType(request.getPosType());
        terminal.setActive(request.getActive());
        terminal.setLastSyncedAt(LocalDateTime.now());

        Terminal saved =
                terminalRepository.save(terminal);

        return terminalMapper.toResponse(saved);
    }

    @Transactional(readOnly = true)
    public TerminalResponse getTerminalByTerminalId(String terminalId) {

        Terminal terminal =
                terminalRepository.findByTerminalId(terminalId)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Terminal not found"));

        return terminalMapper.toResponse(terminal);
    }

    @Transactional(readOnly = true)
    public TerminalResponse getTerminalByTerminalSerialNumber(
            String terminalSerialNumber) {

        Terminal terminal =
                terminalRepository.findByTerminalSerialNumber(
                                terminalSerialNumber)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Terminal not found"));

        return terminalMapper.toResponse(terminal);
    }

    @Transactional(readOnly = true)
    public List<TerminalResponse> getAllTerminals() {

        return terminalMapper.toResponseList(
                terminalRepository.findAllByOrderByTerminalIdAsc()
        );
    }

    @Transactional(readOnly = true)
    public List<TerminalResponse> getActiveTerminals() {

        return terminalMapper.toResponseList(
                terminalRepository.findByActiveTrueOrderByTerminalIdAsc()
        );
    }

}